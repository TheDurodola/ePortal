package com.school.eportal.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VerifySchoolFeesPaymentRequest {

    @NotBlank(message = "Student ID required.")
    private String studentID;

    @NotBlank(message = "Session Required.")
    private String session;
}
