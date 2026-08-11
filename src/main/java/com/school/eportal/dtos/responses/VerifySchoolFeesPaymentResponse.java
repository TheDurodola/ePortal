package com.school.eportal.dtos.responses;

import lombok.*;

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
