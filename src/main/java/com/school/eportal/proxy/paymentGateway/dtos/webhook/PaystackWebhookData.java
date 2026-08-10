package com.school.eportal.proxy.paymentGateway.dtos.webhook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.school.eportal.proxy.paymentGateway.dtos.Customer;
import com.school.eportal.proxy.paymentGateway.dtos.EventLog;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.tomcat.util.http.parser.Authorization;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaystackWebhookData {
    private Long id;
    private String domain;
    private String status;
    private String reference;
    private Long amount;
    private String message;

    @JsonProperty("gateway_response")
    private String gatewayResponse;

    @JsonProperty("paid_at")
    private String paidAt;

    @JsonProperty("created_at")
    private String createdAt;

    private String channel;
    private String currency;

    @JsonProperty("ip_address")
    private String ipAddress;

    private Object metadata;
    private EventLog log;
    private Long fees;
    private Customer customer;
    private Authorization authorization;
    private Object plan;
}
