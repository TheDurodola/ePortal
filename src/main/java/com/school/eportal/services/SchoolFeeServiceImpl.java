package com.school.eportal.services;

import com.school.eportal.data.models.*;
import com.school.eportal.data.models.enums.Department;
import com.school.eportal.data.models.enums.FeeLedgerStatus;
import com.school.eportal.data.models.enums.TransactionStatus;
import com.school.eportal.data.repositories.*;
import com.school.eportal.dtos.SchoolResponseData;
import com.school.eportal.dtos.requests.GetSchoolFeesTransactionsRequest;
import com.school.eportal.dtos.requests.PaySchoolFeesRequest;
import com.school.eportal.dtos.requests.VerifySchoolFeesPaymentRequest;
import com.school.eportal.dtos.responses.CreateSchoolFeesResponse;
import com.school.eportal.dtos.responses.GetSchoolFeesTransactionsResponse;
import com.school.eportal.dtos.responses.PaySchoolFeesResponse;
import com.school.eportal.dtos.responses.VerifySchoolFeesPaymentResponse;
import com.school.eportal.exceptions.*;
import com.school.eportal.proxy.paymentGateway.PaymentGatewayClient;
import com.school.eportal.proxy.paymentGateway.dtos.requests.InitiatePaymentRequest;
import com.school.eportal.proxy.paymentGateway.dtos.responses.InitiatePaymentResponse;
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
import java.time.Instant;
import java.util.List;
import java.util.Objects;

import static com.school.eportal.utils.NairaConverter.koboToNaira;
import static com.school.eportal.utils.NairaConverter.nairaToKobo;
import static com.school.eportal.utils.RandomPicker.generateRandomAlphanumeric;
import static com.school.eportal.utils.Validator.*;

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

    private @NonNull String getSession(String sessionId) {
        Session session = sessions.findById(sessionId).orElse(null);
        assert session != null;
        return session.getStartYear() + "/" + session.getEndYear();
    }

    private BigDecimal getTotal(BigDecimal tuition) {
        return tuition;
    }


    private String createSession(@NonNull String session) {
        int startYearInInteger = Integer.parseInt(session.substring(0, 3));
        if (sessions.findByStartYear(startYearInInteger).isPresent()) {
            return sessions.findByStartYear(startYearInInteger).get().getId();
        }

        Session sessionObj;
        if (isSingleYear(session)) {
            int sessionInInt = Integer.parseInt(session);
            sessionObj = Session.builder().startYear(sessionInInt).endYear(sessionInInt + 1).isCurrent(false).build();
        }
        if (isShortSession(session)) {
            sessionObj = Session.builder().startYear(startYearInInteger).endYear(startYearInInteger + 1).isCurrent(false).build();
        }

        if (isFullSession(session)) {
            sessionObj = Session.builder().startYear(startYearInInteger).endYear(startYearInInteger + 1).isCurrent(false).build();
        } else throw new InvalidSessionException("Input Session could not be parsed into Session Object");

        Session savedSession = sessions.save(sessionObj);

        return savedSession.getId();
    }

    @Override
    @Transactional
    public PaySchoolFeesResponse paySchoolFees(@NonNull PaySchoolFeesRequest request, @NonNull Authentication authentication) {
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

        long total = schoolFees.findBySessionIdAndDepartmentAndGrade(session.getId(), department, classroom.getGrade())
                .orElseThrow(() -> new SchoolFeesException("User Not Found.")).getTotal();

        FeeLedger newFeeLedger = generateNewLedger(student, session, total);
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
        if (feeLedger.getTotalExpectedAmount() < (nairaToKobo(request.getAmount()) + getAmountPaid(feeLedger.getId())) ) {
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


    // For Paystack WebSocket
    @Override
    public void updateTransaction(PaystackWebhookPayload request) {
        String reference = request.getData().getReference();


    }


    @Override
    public VerifySchoolFeesPaymentResponse verifySchoolFees(VerifySchoolFeesPaymentRequest request) {
        return null;
    }


    @Override
    public GetSchoolFeesTransactionsResponse getSchoolFeesTransaction(GetSchoolFeesTransactionsRequest request) {
        return null;
    }
}
