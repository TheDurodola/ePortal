package com.school.eportal.security.dtos.responses;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SignInResponse {
    private String jwt;

    public SignInResponse(String jwt) {
        this.jwt = jwt;
    }
}
