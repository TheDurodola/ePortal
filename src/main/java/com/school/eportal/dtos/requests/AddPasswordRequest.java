package com.school.eportal.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.*;

import java.time.LocalDate;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddPasswordRequest {

    @NotBlank(message = "AdNo/Username cannot be blank")
    private String username;

    @Past(message = "User birthdate can only be in the past")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Password cannot be blank")
    private String password;
}
