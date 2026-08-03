package com.school.eportal.dtos.responses;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class ParentRegistrationResponse {
    private String parentFirstName;
    private String childFirstName;
}
