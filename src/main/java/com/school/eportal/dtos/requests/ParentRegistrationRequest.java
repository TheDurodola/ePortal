package com.school.eportal.dtos.requests;


import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ParentRegistrationRequest {

    @Email(message = "Must be a valid Email Address")
    private String username;

    @NotBlank(message = "Firstname cannot be blank")
    @Size(min = 2, max = 30, message = "Username must be between 2 and 30 characters")
    private String firstName;

    @NotBlank(message = "Lastname cannot be blank")
    @Size(min = 2, max = 30, message = "Username must be between 2 and 30 characters")
    private String lastName;

    @NotNull(message = "User date of birth is required")
    @Past(message = "Date Of Birth must be in the past")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Lastname cannot be blank")
    @Size(min = 2, max = 30, message = "Username must be between 2 and 30 characters")
    private String childSchoolId;

    @NotNull(message = "User date of birth is required")
    @Past(message = "Date Of Birth must be in the past")
    private LocalDate childDateOfBirth;

    @NotBlank(message = "Lastname cannot be blank")
    @Size(min = 5, max = 30, message = "Username must be between 5 and 30 characters")
    private String password;

}
