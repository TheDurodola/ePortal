package com.school.eportal.dtos;

import com.school.eportal.data.models.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class BulkAccountDto {

    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
    private String username;

    @NotBlank(message = "Firstname cannot be blank")
    @Size(min = 2, max = 30, message = "Username must be between 2 and 30 characters")
    private String firstName;

    @NotBlank(message = "Lastname cannot be blank")
    @Size(min = 2, max = 30, message = "Username must be between 2 and 30 characters")
    private String lastName;

    @NotNull(message = "User role is required")
    private Role role;

    @NotNull(message = "User birthdate is required")
    @Past(message = "Birthdate must be in the past")
    private LocalDate birthDate;
}
