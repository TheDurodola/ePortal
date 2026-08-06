package com.school.eportal.services;

import com.school.eportal.data.models.SchoolFee;
import com.school.eportal.data.repositories.SchoolFees;
import com.school.eportal.dtos.excel.SchoolFeeExcelExtractDTO;
import com.school.eportal.dtos.requests.ConfirmSchoolSchoolTransactionRequest;
import com.school.eportal.dtos.requests.GetSchoolFeesTransactionsRequest;
import com.school.eportal.dtos.requests.PaySchoolFeesRequest;
import com.school.eportal.dtos.requests.VerifySchoolFeesPaymentRequest;
import com.school.eportal.dtos.responses.CreateSchoolFeesResponse;
import com.school.eportal.dtos.responses.GetSchoolFeesTransactionsResponse;
import com.school.eportal.dtos.responses.PaySchoolFeesResponse;
import com.school.eportal.dtos.responses.VerifySchoolFeesPaymentResponse;
import com.school.eportal.exceptions.ValidatorException;
import com.school.eportal.services.interfaces.SchoolFeeService;
import com.school.eportal.utils.ExcelParser;
import com.school.eportal.utils.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static com.school.eportal.utils.NairaConverter.nairaToKobo;
import static com.school.eportal.utils.Validator.isValidSessionFormat;
import static com.school.eportal.utils.Validator.validateExcelFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchoolFeeServiceImpl implements SchoolFeeService {

    private final ExcelParser parser;
    private final SchoolFees schoolFees;

    @Override
    public CreateSchoolFeesResponse createSchoolFees(MultipartFile file) {
        List<SchoolFee> fees;
        try {
            validateExcelFile(file);
            fees = parser.parseSchoolFeeExcelFile(file).stream()
                    .map(excelExtract -> SchoolFee
                            .builder()
                                .sessionId(createSession(excelExtract.getSession()))
                                .department(excelExtract.getDepartment())
                                .grade(excelExtract.getGrade())
                                .tuitionInKobo(nairaToKobo(excelExtract.getTuition()))
                                .firstTermMinPercentage(excelExtract.getFirstTermMinPercentage())
                                .secondTermMinPercentage(excelExtract.getSecondTermMinPercentage())
                                .thirdTermMinPercentage(excelExtract.getThirdTermMinPercentage())
                            .build())
                    .toList();

        } catch (IOException e) {
            throw new ValidatorException(e);
        }
        return null;
    }

    private String createSession(String session) {

        return session;
    }

    @Override
    public PaySchoolFeesResponse paySchoolFees(PaySchoolFeesRequest request) {
        return null;
    }

    @Override
    public void confirmSchoolFeesTransaction(ConfirmSchoolSchoolTransactionRequest request) {

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
