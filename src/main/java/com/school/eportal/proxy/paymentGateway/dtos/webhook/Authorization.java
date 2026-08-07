package com.school.eportal.proxy.paymentGateway.dtos.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Authorization {
    @JsonProperty("authorization_code") String authorizationCode;
    @JsonProperty("card_type") String cardType;
    String last4;
    @JsonProperty("exp_month") String expMonth;
    @JsonProperty("exp_year") String expYear;
    String bin;
    String bank;
    @JsonProperty("country_code") String countryCode;
    String brand;
    Boolean reusable;
    String signature;
}
