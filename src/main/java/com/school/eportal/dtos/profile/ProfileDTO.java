package com.school.eportal.dtos.profile;

import com.school.eportal.data.models.enums.Role;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public  class ProfileDTO {
    private String firstName;
    private String lastName;
    private String username;
    private Role role;
    private LocalDate dateOfBirth;
    private List<StudentDTO> students;
    private List<TeacherDTO> teachers;
}
