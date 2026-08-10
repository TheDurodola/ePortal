package com.school.eportal.proxy.paymentGateway.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Authorization {
    @JsonProperty("authorization_code")
    private String authorizationCode;

    private String bin;
    private String last4;

    @JsonProperty("exp_month")
    private String expMonth;

    @JsonProperty("exp_year")
    private String expYear;

    @JsonProperty("card_type")
    private String cardType;

    private String bank;

    @JsonProperty("country_code")
    private String countryCode;

    private String brand;

    @JsonProperty("account_name")
    private String accountName;
}