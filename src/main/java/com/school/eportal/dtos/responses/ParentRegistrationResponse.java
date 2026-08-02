package com.school.eportal.dtos.responses;

import lombok.Builder;

@Builder
public class ParentRegistrationResponse {
    private String parentFirstName;
    private String childFirstName;
}
