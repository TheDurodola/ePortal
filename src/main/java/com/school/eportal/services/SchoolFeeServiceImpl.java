package com.school.eportal.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.school.eportal.data.models.*;
import com.school.eportal.data.models.enums.*;
import com.school.eportal.data.repositories.*;
import com.school.eportal.dtos.DeptGrade;
import com.school.eportal.dtos.SchoolFeesDetailPayload;
import com.school.eportal.dtos.SchoolResponseData;
import com.school.eportal.dtos.requests.PaySchoolFeesRequest;
import com.school.eportal.dtos.requests.PaystackWebhookRequest;
import com.school.eportal.dtos.requests.VerifySchoolFeesPaymentRequest;
import com.school.eportal.dtos.responses.CreateSchoolFeesResponse;
import com.school.eportal.dtos.responses.GetSchoolFeesDetailsResponse;
import com.school.eportal.dtos.responses.PaySchoolFeesResponse;
import com.school.eportal.dtos.responses.VerifySchoolFeesPaymentResponse;
import com.school.eportal.exceptions.*;
import com.school.eportal.proxy.paymentGateway.PaymentGatewayClient;
import com.school.eportal.proxy.paymentGateway.dtos.requests.InitiatePaymentRequest;
import com.school.eportal.proxy.paymentGateway.dtos.responses.InitiatePaymentResponse;
import com.school.eportal.proxy.paymentGateway.dtos.webhook.PaystackWebhookData;
import com.school.eportal.proxy.paymentGateway.dtos.webhook.PaystackWebhookPayload;
import com.school.eportal.services.interfaces.SchoolFeeService;
import com.school.eportal.utils.ExcelParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.school.eportal.proxy.paymentGateway.PaystackPaymentGatewayImpl.isValidSignature;
import static com.school.eportal.utils.BigDecimalUtils.greaterThanOrEqualsTo;
import static com.school.eportal.utils.NairaConverter.koboToNaira;
import static com.school.eportal.utils.NairaConverter.nairaToKobo;
import static com.school.eportal.utils.NameFormatter.toProperCase;
import static com.school.eportal.utils.RandomPicker.generateRandomAlphanumeric;
import static com.school.eportal.utils.Validator.*;
import static java.lang.Integer.parseInt;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchoolFeeServiceImpl implements SchoolFeeService {

    private final ExcelParser parser;
    private final SchoolFees schoolFees;
    private final Sessions sessions;
    private final FeeLedgers feeLedgers;
    private final FeeTransactions feeTransactions;
    private final Accounts accounts;
    private final DepartmentPathRepo departmentPathRepo;
    private final Classrooms classrooms;
    private final PaymentGatewayClient paymentGatewayClient;
    private final ObjectMapper objectMapper;
    private final ParentChildRepo parentChildRepo;

    @Override
    public CreateSchoolFeesResponse createSchoolFees(MultipartFile file) {
        List<SchoolFee> fees;
        try {
            validateExcelFile(file);
            fees = parser.parseSchoolFeeExcelFile(file).stream()
                    .map(excelExtract ->
                            SchoolFee
                                    .builder()
                                    .sessionId(createSession(excelExtract.getSession()))
                                    .department(excelExtract.getDepartment())
                                    .grade(excelExtract.getGrade())
                                    .tuitionInKobo(nairaToKobo(excelExtract.getTuition()))
                                    .total(nairaToKobo(getTotal(excelExtract.getTuition())))
                                    .firstTermMinPercentage(excelExtract.getFirstTermMinPercentage())
                                    .secondTermMinPercentage(excelExtract.getSecondTermMinPercentage())
                                    .thirdTermMinPercentage(excelExtract.getThirdTermMinPercentage())
                                    .build())
                    .toList();

        } catch (IOException e) {
            throw new ValidatorException(e);
        }
        List<SchoolFee> savedSchoolFees = schoolFees.saveAll(fees);

        List<SchoolResponseData> data = savedSchoolFees.stream().map(fee -> SchoolResponseData.builder()
                        .session(getSession(fee.getSessionId()))
                        .department(fee.getDepartment())
                        .grade(fee.getGrade())
                        .tuition(koboToNaira(fee.getTuitionInKobo()))
                        .total(koboToNaira(fee.getTotal()))
                        .firstTermMinPercentage(fee.getFirstTermMinPercentage())
                        .secondTermMinPercentage(fee.getSecondTermMinPercentage())
                        .thirdTermMinPercentage(fee.getThirdTermMinPercentage())
                        .build())
                .toList();

        return CreateSchoolFeesResponse.builder()
                .count(data.size())
                .data(data)
                .build();
    }

    @Override
    @Transactional
    public PaySchoolFeesResponse paySchoolFees(@NonNull PaySchoolFeesRequest request, @NonNull Authentication authentication) {

        log.info("The student id is {}", request.getStudentId());
        Account parent = accounts.findById(Objects.requireNonNull(authentication.getPrincipal()).toString()
        ).orElseThrow(() -> new UserNotFoundException("Parent account not found"));

        Session session = sessions.findByIsCurrentTrue()
                .orElseThrow(() -> new InvalidSessionException("Payment cannot be made yet as the session is not yet publish."));
        Account student = accounts.findById(request.getStudentId())
                .orElseThrow(() -> new UserNotFoundException("User Not Found."));
        String reference = generateSchoolFeesReference(session);

        List<FeeLedger> outstandings = feeLedgers.findByStudentIdAndStatusOrStatus(request.getStudentId(),
                FeeLedgerStatus.UNPAID, FeeLedgerStatus.PARTIALLY_PAID);
        if (!outstandings.isEmpty()) {
            if (outstandings.size() == 1) {
                FeeLedger feeLedger = outstandings.getFirst();
                IfRequestAmountExceedsSchoolFeesTotalAmount(request, feeLedger);

                addNewTransaction(request, reference, parent, feeLedger);

                InitiatePaymentResponse paystackResponse = paymentGatewayClient.initiatePayment(InitiatePaymentRequest.builder()
                        .reference(reference)
                        .email(parent.getUsername())
                        .amount(nairaToKobo(request.getAmount()))
                        .build());

                feeLedgers.save(feeLedger);

                return PaySchoolFeesResponse.builder()
                        .redirectUrl(paystackResponse.getData().authorizationUrl())
                        .build();
            } else
                throw new OutstandingSchoolFeesException(student.getLastName() + " " + student.getFirstName() + "hasn't been cleared for multiple" +
                        " sessions. Kindly reach out to management.");
        }

        Classroom classroom = classrooms.findByStudentsContaining(student.getId())
                .orElseThrow(() -> new NoSuchClassroomException("No Class Room Found for this student."));

        Department department = getDepartment(student);

        SchoolFee schoolFee = schoolFees.findBySessionIdAndDepartmentAndGrade(session.getId(), department, classroom.getGrade())
                .orElseThrow(() -> new SchoolFeesException("User Not Found."));

        FeeLedger newFeeLedger = generateNewLedger(student, session, schoolFee.getTotal());
        newFeeLedger.setSchoolFeesId(schoolFee.getId());
        newFeeLedger.getTransactions().add(reference);

        FeeLedger savedLedger = feeLedgers.save(newFeeLedger);

        InitiatePaymentResponse response = paymentGatewayClient.initiatePayment(InitiatePaymentRequest.builder()
                .email(parent.getUsername())
                .amount(nairaToKobo(request.getAmount()))
                .reference(reference)
                .build());

        addNewTransaction(request, reference, parent, savedLedger);
        return PaySchoolFeesResponse.builder()
                .redirectUrl(response.getData().authorizationUrl())
                .build();
    }

    @Override
    public void updateTransaction(@NonNull PaystackWebhookRequest request) {

        if (!isValidSignature(request.getRawPayload(), request.getSignature())) {
            throw new InvalidWebhookSignature("Invalid Webhook Signature");
        }

        PaystackWebhookPayload payload;
        try {
            payload = objectMapper.readValue(request.getRawPayload(), PaystackWebhookPayload.class);
        } catch (JsonProcessingException e) {
            throw new ParsingException(e.getMessage());
        }

        String event = payload.getEvent();
        PaystackWebhookData data = payload.getData();

        FeeTransaction feeTransaction = feeTransactions.findByPaymentReference(data.getReference())
                .orElseThrow(() -> new FeeTransactionDoesntExistException("Fee Transaction doesnt exist."));

        if (event.equals("charge.success")) {
            feeTransaction.setStatus(TransactionStatus.CONFIRMED);
            updatedFeeLedgerStatus(feeTransaction);

        } else if (event.equals("charge.failed")) {
            feeTransaction.setStatus(TransactionStatus.FAILED);
        }

        feeTransactions.save(feeTransaction);

        //TODO: Remember to save the payload

    }

    @Override
    public VerifySchoolFeesPaymentResponse verifySchoolFees(@NonNull VerifySchoolFeesPaymentRequest request) {
        Account student = accounts.findByUsername(request.getStudentID()).orElseThrow(() -> new UserNotFoundException("User Not Found."));
        if (!student.getRole().equals(Role.STUDENT)) {
            throw new InvalidRoleException("This School ID doesn't belong to a student");
        }
        String startYear = request.getSession().substring(0, 3);
        int sessionYear = parseInt(startYear);

        Session session = sessions.findByStartYear(sessionYear)
                .orElseThrow(() -> new AcademicSessionDoesntExistException("The Academic Session doesn't exist."));

        FeeLedger feeLedger = feeLedgers.findByStudentIdAndAcademicSessionId(student.getId(), session.getId())
                .orElseThrow(() -> new FeeLedgerDoesntExistException("User Not Found."));

        long totalAmountPaid = getTotalAmountPaid(feeLedger);

        SchoolFee schoolFee = schoolFees.findById(feeLedger.getSchoolFeesId())
                .orElseThrow(() -> new SchoolFeesException("School Fees doesn't exist."));

        return VerifySchoolFeesPaymentResponse.builder()
                .studentFirstName(toProperCase(student.getFirstName()))
                .studentLastName(toProperCase(student.getLastName()))
                .qualifiedForFirstTerm(qualifiedForFirstTerm(totalAmountPaid, schoolFee))
                .qualifiedForSecondTerm(qualifiedForSecondTerm(totalAmountPaid, schoolFee))
                .qualifiedForThirdTerm(qualifiedForThirdTerm(totalAmountPaid, schoolFee))
                .build();

    }

    @Override
    public GetSchoolFeesDetailsResponse getSchoolFeesDetails(@NonNull Authentication authentication) {

        Account account = accounts.findById(Objects.requireNonNull(authentication.getPrincipal()).toString())
                .orElseThrow(() -> new UserNotFoundException("User not found."));

        if (account.getRole().equals(Role.PARENT)) {
            List<ParentChild> parentChildRelationship = parentChildRepo.findAllByParent(account.getId());
            if (parentChildRelationship.isEmpty()) {
                throw new ParentChildRelationshipException("This parent currently has no child assigned");
            }
            List<String> childIDs = parentChildRelationship.stream()
                    .map(ParentChild::getChild).toList();

            List<SchoolFeesDetailPayload> outstandingSchoolFeesPayload = getOutstandingSchoolFeesID(childIDs);


            List<SchoolFeesDetailPayload> recordsOfStudentsWithoutOutstandings = getRecordsOfStudentsWithoutOutstandings(outstandingSchoolFeesPayload, childIDs);

            List<SchoolFeesDetailPayload> allChildrenFeeDetails = Stream.concat(
                    outstandingSchoolFeesPayload.stream(),
                    recordsOfStudentsWithoutOutstandings.stream()
            ).toList();
            return GetSchoolFeesDetailsResponse.builder()
                    .data(allChildrenFeeDetails)
                    .build();
        }
        return GetSchoolFeesDetailsResponse.builder()

                .build();
    }


    private @NonNull String getSession(String sessionId) {
        Session session = sessions.findById(sessionId).orElse(null);
        assert session != null;
        return session.getStartYear() + "/" + session.getEndYear();
    }

    private BigDecimal getTotal(BigDecimal tuition) {
        return tuition;
    }

    private String createSession(@NonNull String session) {
        log.info(session);
        int startYearInInteger = parseInt(session.substring(0, 4));
        log.info("Start Year: {}", startYearInInteger);
        if (sessions.findByStartYear(startYearInInteger).isPresent()) {
            return sessions.findByStartYear(startYearInInteger).get().getId();
        }

        Session sessionObj;
        if (isSingleYear(session)) {
            int sessionInInt = parseInt(session);
            sessionObj = Session.builder().startYear(sessionInInt).endYear(sessionInInt + 1).isCurrent(false).build();
        } else if (isShortSession(session)) {
            sessionObj = Session.builder().startYear(startYearInInteger).endYear(startYearInInteger + 1).isCurrent(false).build();
        } else if (isFullSession(session)) {
            sessionObj = Session.builder().startYear(startYearInInteger).endYear(startYearInInteger + 1).isCurrent(false).build();
        } else if (isFullSessionWithoutSlash(session)) {
            sessionObj = Session.builder().startYear(startYearInInteger).endYear(startYearInInteger + 1).isCurrent(false).build();
        } else throw new InvalidSessionException("Input Session could not be parsed into Session Object");

        Session savedSession = sessions.save(sessionObj);

        return savedSession.getId();
    }

    private void addNewTransaction(@NonNull PaySchoolFeesRequest request, String reference, @NonNull Account parent, @NonNull FeeLedger newFeeLedger) {
        FeeTransaction firstTransaction = FeeTransaction.builder()
                .amount(nairaToKobo(request.getAmount()))
                .attemptedAt(Instant.now())
                .status(TransactionStatus.PENDING)
                .paymentReference(reference)
                .feeLedger(newFeeLedger.getId())
                .madeBy(parent.getFirstName() + " " + parent.getLastName())
                .build();

        feeTransactions.save(firstTransaction);
    }

    private void IfRequestAmountExceedsSchoolFeesTotalAmount(@NonNull PaySchoolFeesRequest request, @NonNull FeeLedger feeLedger) {
        if (feeLedger.getTotalExpectedAmount() < (nairaToKobo(request.getAmount()) + getAmountPaid(feeLedger.getId()))) {
            throw new InvalidAmountException("Exceeded total amount");
        }
    }

    private long getAmountPaid(String id) {
        List<FeeTransaction> transactions = feeTransactions.findByFeeLedger(id);
        return transactions.stream()
                .filter(t -> t.getStatus() == TransactionStatus.CONFIRMED)
                .mapToLong(FeeTransaction::getAmount)
                .sum();
    }

    private static FeeLedger generateNewLedger(@NonNull Account student, @NonNull Session session, long total) {
        return FeeLedger.builder().studentId(student.getId())
                .academicSessionId(session.getId())
                .totalExpectedAmount(total)
                .status(FeeLedgerStatus.UNPAID)
                .build();
    }

    private Department getDepartment(@NonNull Account student) {
        Department department;
        try {
            department = departmentPathRepo.findByStudentsContaining(student.getId())
                    .orElseThrow(() -> new UserNotFoundException("User Not Found.")).getDepartment();
        } catch (UserNotFoundException e) {
            department = Department.NONE;
        }
        return department;
    }

    private static @NonNull String generateSchoolFeesReference(@NonNull Session session) {
        return session.getStartYear() + generateRandomAlphanumeric() + session.getEndYear();
    }

    private void updatedFeeLedgerStatus(@NonNull FeeTransaction feeTransaction) {
        FeeLedger feeLedger = feeLedgers.findById(feeTransaction.getFeeLedger())
                .orElseThrow(() -> new FeeLedgerDoesntExistException("Fee Ledger doesn't exist."));

        long total = getTotalAmountPaid(feeLedger, feeTransaction);

        if (feeLedger.getTotalExpectedAmount() > total && total > 0) {
            feeLedger.setStatus(FeeLedgerStatus.PARTIALLY_PAID);
        }
        if (total == feeLedger.getTotalExpectedAmount()) {
            feeLedger.setStatus(FeeLedgerStatus.FULLY_PAID);
        }
        if (total < feeLedger.getTotalExpectedAmount()) {
            feeLedger.setStatus(FeeLedgerStatus.OVERPAID);
        }
        feeTransactions.save(feeTransaction);
    }

    private long getTotalAmountPaid(@NonNull FeeLedger feeLedger, @NonNull FeeTransaction feeTransaction) {
        List<FeeTransaction> transactions = feeTransactions.findByFeeLedger(feeLedger.getId());
        long totalConfirmed = transactions.stream()
                .filter(transaction -> transaction.getStatus() == TransactionStatus.CONFIRMED)
                .mapToLong(FeeTransaction::getAmount)
                .sum();
        return feeTransaction.getAmount() + totalConfirmed;
    }

    private boolean qualifiedForFirstTerm(long totalAmountPaid, @NonNull SchoolFee schoolFee) {
        BigDecimal percentage = schoolFee.getFirstTermMinPercentage();
        long totalExpected = schoolFee.getTotal();

        BigDecimal percentageInFigures = percentage.divide(BigDecimal.valueOf(100), 3, RoundingMode.HALF_UP);

        BigDecimal bigDecimal = koboToNaira(totalExpected);
        BigDecimal cutOff = bigDecimal.multiply(percentageInFigures);

        return greaterThanOrEqualsTo(koboToNaira(totalAmountPaid), cutOff);
    }

    private boolean qualifiedForSecondTerm(long totalAmountPaid, @NonNull SchoolFee schoolFee) {
        BigDecimal percentage = schoolFee.getSecondTermMinPercentage();
        long totalExpected = schoolFee.getTotal();

        BigDecimal percentageInFigures = percentage.divide(BigDecimal.valueOf(100), 3, RoundingMode.HALF_UP);

        BigDecimal bigDecimal = koboToNaira(totalExpected);
        BigDecimal cutOff = bigDecimal.multiply(percentageInFigures);

        return greaterThanOrEqualsTo(koboToNaira(totalAmountPaid), cutOff);
    }

    private boolean qualifiedForThirdTerm(long totalAmountPaid, @NonNull SchoolFee schoolFee) {
        BigDecimal percentage = schoolFee.getThirdTermMinPercentage();
        long totalExpected = schoolFee.getTotal();

        BigDecimal percentageInFigures = percentage.divide(BigDecimal.valueOf(100), 3, RoundingMode.HALF_UP);

        BigDecimal bigDecimal = koboToNaira(totalExpected);
        BigDecimal cutOff = bigDecimal.multiply(percentageInFigures);

        return greaterThanOrEqualsTo(koboToNaira(totalAmountPaid), cutOff);
    }

    private long getTotalAmountPaid(@NonNull FeeLedger feeLedger) {
        return feeTransactions.findByFeeLedger(feeLedger.getId()).stream()
                .filter(feeTransaction -> feeTransaction.getStatus() == TransactionStatus.CONFIRMED)
                .mapToLong(FeeTransaction::getAmount)
                .sum();
    }

    private List<SchoolFeesDetailPayload> getRecordsOfStudentsWithoutOutstandings(@NonNull List<SchoolFeesDetailPayload> outstandingSchoolFeesPayload, @NonNull List<String> childIDs) {
        Set<String> studentIdsWithOutstanding = outstandingSchoolFeesPayload.stream()
                .map(SchoolFeesDetailPayload::getStudentID)
                .collect(Collectors.toSet());

        List<String> childIdsWithNoOutstanding = childIDs.stream()
                .filter(id -> !studentIdsWithOutstanding.contains(id))
                .toList();


        List<Account> children = accounts.findAllById(childIdsWithNoOutstanding);

        return children.stream()
                .map(child -> SchoolFeesDetailPayload.builder()
                        .studentFirstName(toProperCase(child.getFirstName()))
                        .studentID(child.getId())
                        .studentLastName(toProperCase(child.getLastName()))
                        .grade(classrooms.findByStudentsContaining(child.getId())
                                .orElseThrow(() -> new InvalidClassroomException(child.getFirstName() + " doesn't have a class yet"))
                                .getGrade())
                        .department(getStudentDepartment(child))
                        .session(getCurrentSessionInString())
                        .totalPaid(BigDecimal.ZERO)
                        .build())

                .map(payload -> {
                    SchoolFee fee = schoolFees.findBySessionIdAndDepartmentAndGrade(
                            sessions.findByIsCurrentTrue().orElseThrow().getId(),
                            payload.getDepartment(),
                            payload.getGrade()
                    ).orElseThrow(() -> new InvalidSchoolSessionException("Invalid school session"));

                    return SchoolFeesDetailPayload.builder()
                            .studentID(payload.getStudentID())
                            .studentFirstName(payload.getStudentFirstName())
                            .studentLastName(payload.getStudentLastName())
                            .grade(payload.getGrade())
                            .department(payload.getDepartment())
                            .session(payload.getSession())
                            .tuition(koboToNaira(fee.getTuitionInKobo()))
                            .total(koboToNaira(fee.getTotal()))
                            .totalPaid(BigDecimal.ZERO)
                            .build();
                })
                .toList();
    }

    private @NonNull String getCurrentSessionInString() {
        Session session = sessions.findByIsCurrentTrue().orElseThrow(() -> new InvalidSchoolSessionException("No valid session"));

        return session.getStartYear() + "/" + session.getEndYear();
    }

    private @NonNull Department getStudentDepartment(@NonNull Account child) {
        Optional<DepartmentPath> byStudentsContaining = departmentPathRepo.findByStudentsContaining(child.getId());
        if (byStudentsContaining.isEmpty()) {
            return Department.NONE;
        }
        return byStudentsContaining.get().getDepartment();
    }

    private List<SchoolFeesDetailPayload> getOutstandingSchoolFeesID(List<String> childIDs) {

        // 1. Fetch all outstanding ledgers for these students in one query, correctly filtered
        List<FeeLedger> ledgers = feeLedgers.findByStudentIdInAndStatusIn(
                childIDs,
                List.of(FeeLedgerStatus.UNPAID, FeeLedgerStatus.PARTIALLY_PAID)
        );

        // 2. Key ledgers by studentId for O(1) correlation (assumes one outstanding ledger per student;
        //    if a student can have multiple, this needs to become a Map<String, List<FeeLedger>>)
        Map<String, FeeLedger> ledgerByStudentId = ledgers.stream()
                .collect(Collectors.toMap(FeeLedger::getStudentId, l -> l));

        // 3. Fetch accounts and schoolFees, keyed by their own IDs — never rely on findAllById ordering
        Map<String, Account> accountById = accounts.findAllById(ledgerByStudentId.keySet()).stream()
                .collect(Collectors.toMap(Account::getId, a -> a));

        List<String> schoolFeeIds = ledgers.stream().map(FeeLedger::getSchoolFeesId).toList();
        Map<String, SchoolFee> schoolFeeById = schoolFees.findAllById(schoolFeeIds).stream()
                .collect(Collectors.toMap(SchoolFee::getId, sf -> sf));

        // 4. Join explicitly by key, not by position
        return ledgerByStudentId.values().stream()
                .map(ledger -> {
                    Account account = accountById.get(ledger.getStudentId());
                    SchoolFee fee = schoolFeeById.get(ledger.getSchoolFeesId());

                    if (account == null || fee == null) {
                        throw new IllegalStateException(
                                "Missing correlated data for studentId=" + ledger.getStudentId());
                    }

                    DeptGrade deptGrade = getDeptGrade(account);
                    return SchoolFeesDetailPayload.builder()
                            .session(ledger.getAcademicSessionId())
                            .studentID(account.getId())
                            .department(deptGrade.department())
                            .grade(deptGrade.grade())
                            .studentFirstName(account.getFirstName())
                            .studentLastName(account.getLastName())
                            .tuition(koboToNaira(fee.getTuitionInKobo()))
                            .total(koboToNaira(fee.getTotal()))
                            .totalPaid(koboToNaira(getTotalAmountPaid(ledger)))
                            .build();
                })
                .toList();
    }

    private DeptGrade getDeptGrade(Account account) {

        Classroom classroom = getStudentClassroom(account);
        Department department = getDepartment(account);

        return new DeptGrade(department, classroom.getGrade());
    }

    private Classroom getStudentClassroom(Account account) {
        return classrooms.findByStudentsContaining(account.getId()).orElseThrow(() -> new InvalidClassroomException(account.getFirstName() + " doesn't have a class yet"));
    }
}
