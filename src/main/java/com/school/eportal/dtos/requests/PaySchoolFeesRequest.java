package com.school.eportal.dtos.requests;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
public class PaySchoolFeesRequest {

    @NotBlank(message = "ID is required.")
    private String studentId;

    @NotNull(message = "Amount is required.")
    @DecimalMin(value = "100", message = "Amount must be greater than one hundred Naira.")
    @Digits(integer = 12, fraction = 2, message = "Amount format is invalid (max 2 decimal places).")
    private BigDecimal amount;
}
