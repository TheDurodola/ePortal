package com.school.eportal.dtos.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class PaystackWebhookRequest {
    private String signature;
    private String rawPayload;

}
