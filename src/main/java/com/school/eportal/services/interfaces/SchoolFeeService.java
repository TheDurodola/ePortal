package com.school.eportal.services.interfaces;

import com.school.eportal.dtos.requests.GetSchoolFeesTransactionsRequest;
import com.school.eportal.dtos.requests.PaySchoolFeesRequest;
import com.school.eportal.dtos.requests.VerifySchoolFeesPaymentRequest;
import com.school.eportal.dtos.responses.CreateSchoolFeesResponse;
import com.school.eportal.dtos.responses.GetSchoolFeesTransactionsResponse;
import com.school.eportal.dtos.responses.PaySchoolFeesResponse;
import com.school.eportal.dtos.responses.VerifySchoolFeesPaymentResponse;
import com.school.eportal.proxy.paymentGateway.dtos.webhook.PaystackWebhookPayload;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

public interface SchoolFeeService {
        CreateSchoolFeesResponse createSchoolFees(MultipartFile file);
        PaySchoolFeesResponse paySchoolFees(PaySchoolFeesRequest request, Authentication authentication);
        void updateTransaction(PaystackWebhookPayload request);
        VerifySchoolFeesPaymentResponse verifySchoolFees(VerifySchoolFeesPaymentRequest request);
        GetSchoolFeesTransactionsResponse getSchoolFeesTransaction(GetSchoolFeesTransactionsRequest request);
}
