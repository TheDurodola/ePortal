package com.school.eportal.dtos;

import com.school.eportal.data.models.enums.Department;
import com.school.eportal.data.models.enums.Division;
import com.school.eportal.data.models.enums.Grade;
import com.school.eportal.data.models.enums.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class StudentExcelDTOResponseBody {
    private String firstName;
    private String lastName;
    private String schoolId;
    private Role role;
    private Grade grade;
    private Division  division;
    private Department department;
}
