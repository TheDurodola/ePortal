package com.school.eportal.proxy.paymentGateway.dtos.responses;

import com.school.eportal.proxy.paymentGateway.dtos.PaymentInitiationData;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InitiatePaymentResponse {
    private boolean status;
    private String message;
    private PaymentInitiationData data;
}
