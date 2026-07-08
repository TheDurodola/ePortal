package com.school.eportal.security.dtos.requests;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SignInRequest {
    private String username;
    private String password;
}
