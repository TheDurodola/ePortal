package com.school.eportal.proxy.paymentGateway.dtos.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.tomcat.util.http.parser.Authorization;

import java.time.OffsetDateTime;
import java.util.Map;

public class PaystackWebhookData {
    private Long id;
    String domain;
    String status;
    String reference;
    Long amount;
    String message;
    @JsonProperty("gateway_response") String gatewayResponse;
    @JsonProperty("paid_at") OffsetDateTime paidAt;
    @JsonProperty("createdAt")
    OffsetDateTime createdAt;
    String channel;
    String currency;
    Map<String, Object> metadata;
    Customer customer;
    Authorization authorization;
}
