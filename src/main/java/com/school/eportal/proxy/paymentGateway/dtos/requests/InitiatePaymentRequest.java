package com.school.eportal.proxy.paymentGateway.dtos.requests;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class InitiatePaymentRequest {
    private String email;
    private long amount;
    private String reference;
    private String callback_url;
}
