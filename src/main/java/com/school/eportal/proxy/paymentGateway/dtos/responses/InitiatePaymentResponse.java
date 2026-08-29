package com.school.eportal.proxy.paymentGateway.dtos.responses;

import com.school.eportal.proxy.paymentGateway.dtos.PaymentInitiationData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InitiatePaymentResponse {
    private boolean status;
    private String message;
    private PaymentInitiationData data;
}
