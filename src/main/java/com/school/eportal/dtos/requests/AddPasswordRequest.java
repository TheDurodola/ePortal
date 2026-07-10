package com.school.eportal.dtos.requests;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@Builder
public class AddPasswordRequest {
    private String username;
    private LocalDate dateOfBirth;
    private String password;
}
