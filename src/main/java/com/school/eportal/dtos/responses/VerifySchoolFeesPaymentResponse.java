package com.school.eportal.dtos.responses;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class VerifySchoolFeesPaymentResponse {
    private String studentFirstName;
    private String studentLastName;
    private boolean qualifiedForFirstTerm;
    private boolean qualifiedForSecondTerm;
    private boolean qualifiedForThirdTerm;
}
