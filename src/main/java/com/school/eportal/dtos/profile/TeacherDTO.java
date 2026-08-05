package com.school.eportal.dtos.profile;

import com.school.eportal.data.models.enums.Division;
import com.school.eportal.data.models.enums.Grade;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TeacherDTO {
    private String id;
    private String firstName;
    private String lastName;
    private Grade grade;
    private Division division;
}
