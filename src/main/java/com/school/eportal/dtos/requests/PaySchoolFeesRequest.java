package com.school.eportal.dtos.requests;

import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaySchoolFeesRequest {
    private String studentId;
    private BigDecimal amount;
}
