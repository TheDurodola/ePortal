package com.school.eportal.proxy.paymentGateway.dtos.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Customer {
    Long id;
    @JsonProperty("first_name") String firstName;
    @JsonProperty("last_name") String lastName;
    String email;
    @JsonProperty("customer_code") String customerCode;
}
