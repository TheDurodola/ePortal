package com.school.eportal.security.dtos.responses;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.school.eportal.data.models.enums.AccountStatus;
import com.school.eportal.data.models.enums.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@Builder
public class AccountResponse {
    private String id;
    private String username;
    @JsonIgnore
    private String password;
    private Role role;
    private AccountStatus accountStatus;
}
