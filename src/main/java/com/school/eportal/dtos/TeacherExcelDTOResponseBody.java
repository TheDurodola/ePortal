package com.school.eportal.dtos;

import com.school.eportal.data.models.enums.Division;
import com.school.eportal.data.models.enums.Grade;
import com.school.eportal.data.models.enums.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TeacherExcelDTOResponseBody {
    private String username;
    private String firstName;
    private String lastName;
    private Role role;
    private Grade grade;
    private Division division;
}
