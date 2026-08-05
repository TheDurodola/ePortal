package com.school.eportal.proxy.paymentGateway.dtos;

public record PaymentInitiationData(
        String authorizationUrl,
        String accessCode,
        String reference
) {}