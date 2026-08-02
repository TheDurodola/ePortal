package com.school.eportal.dtos;

import com.school.eportal.data.models.Account;
import com.school.eportal.data.models.enums.Division;
import com.school.eportal.data.models.enums.Grade;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class TeacherExcelDTO {
    private Account account;
    private Grade grade;
    private Division division;
}
