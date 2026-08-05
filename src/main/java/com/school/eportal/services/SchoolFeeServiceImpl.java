package com.school.eportal.services;

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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.school.eportal.utils.Validator.validateExcelFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchoolFeeServiceImpl implements SchoolFeeService {

    private final ExcelParser parser;

    @Override
    public CreateSchoolFeesResponse createSchoolFees(MultipartFile file) {
        try {
            validateExcelFile(file);
            parser.parseSchoolFeeExcelFile(file);
        } catch (IOException e) {
            throw new ValidatorException(e);
        }
        return null;
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
