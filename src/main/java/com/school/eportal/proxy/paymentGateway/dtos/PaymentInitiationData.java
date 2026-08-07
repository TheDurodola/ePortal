package com.school.eportal.proxy.paymentGateway.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PaymentInitiationData(
        @JsonProperty("authorization_url") String authorizationUrl,
        @JsonProperty("access_code") String accessCode,
        String reference
) {}