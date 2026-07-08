package com.school.eportal.security.dtos.responses;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SignInResponse {
    private String jwt;
    private String authority;
    public SignInResponse(String jwt, String authorities) {
        this.jwt = jwt;
        this.authority = authorities;
    }
}
