package com.school.eportal.proxy.paymentGateway.dtos.responses;

import com.school.eportal.proxy.paymentGateway.dtos.PaymentInitiationData;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class InitiatePaymentResponse {
    private boolean status;
    private String message;
    private PaymentInitiationData data;
}
