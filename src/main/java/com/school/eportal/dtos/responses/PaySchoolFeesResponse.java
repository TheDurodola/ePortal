package com.school.eportal.dtos.responses;

import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaySchoolFeesResponse {
    private String redirectUrl;
}
