package com.school.eportal.proxy.paymentGateway;

import com.school.eportal.proxy.paymentGateway.dtos.requests.InitiatePaymentRequest;
import com.school.eportal.proxy.paymentGateway.dtos.responses.InitiatePaymentResponse;


public interface PaymentGatewayClient {
    InitiatePaymentResponse initiatePayment(InitiatePaymentRequest request);

}
