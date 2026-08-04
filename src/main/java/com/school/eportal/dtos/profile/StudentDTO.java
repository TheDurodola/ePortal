package com.school.eportal.dtos.profile;

import com.school.eportal.data.models.enums.Department;
import com.school.eportal.data.models.enums.Division;
import com.school.eportal.data.models.enums.Grade;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class StudentDTO {
    private String id;
    private String firstName;
    private String lastName;
    private Grade grade;
    private Division division;
    private Department department;
}
