package com.school.eportal.dtos.excel;

import com.school.eportal.data.models.enums.Department;
import com.school.eportal.data.models.enums.Grade;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class SchoolFeeExcelExtractDTO {
    private String schoolName;
    private Department department;
    private Grade grade;
    private long tuition;
}
