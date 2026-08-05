package com.school.eportal.services.interfaces;

import com.school.eportal.dtos.requests.ConfirmSchoolSchoolTransactionRequest;
import com.school.eportal.dtos.requests.GetSchoolFeesTransactionsRequest;
import com.school.eportal.dtos.requests.PaySchoolFeesRequest;
import com.school.eportal.dtos.requests.VerifySchoolFeesPaymentRequest;
import com.school.eportal.dtos.responses.CreateSchoolFeesResponse;
import com.school.eportal.dtos.responses.GetSchoolFeesTransactionsResponse;
import com.school.eportal.dtos.responses.PaySchoolFeesResponse;
import com.school.eportal.dtos.responses.VerifySchoolFeesPaymentResponse;
import org.springframework.web.multipart.MultipartFile;

public interface SchoolFeeService {
        CreateSchoolFeesResponse createSchoolFees(MultipartFile file);
        PaySchoolFeesResponse paySchoolFees(PaySchoolFeesRequest request);
        void confirmSchoolFeesTransaction(ConfirmSchoolSchoolTransactionRequest request);
        VerifySchoolFeesPaymentResponse verifySchoolFees(VerifySchoolFeesPaymentRequest request);
        GetSchoolFeesTransactionsResponse getSchoolFeesTransaction(GetSchoolFeesTransactionsRequest request);
}
