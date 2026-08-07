package com.school.eportal.proxy.paymentGateway.dtos.webhook;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PaystackWebhookPayload {
    private String event;
    private PaystackWebhookData data;
}
