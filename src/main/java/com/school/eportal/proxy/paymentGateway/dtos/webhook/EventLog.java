package com.school.eportal.proxy.paymentGateway.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventLog {
    @JsonProperty("time_spent")
    private Integer timeSpent;

    private Integer attempts;
    private String authentication;
    private Integer errors;
    private Boolean success;
    private Boolean mobile;
    private List<Object> input;
    private String channel;
    private List<LogHistory> history;
}