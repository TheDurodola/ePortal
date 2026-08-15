package com.school.eportal.dtos.responses;

import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaySchoolFeesResponse {
    private String redirectUrl;
}
