package com.school.eportal.dtos.excel;

import com.school.eportal.data.models.enums.Department;
import com.school.eportal.data.models.enums.Grade;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@Builder
public class SchoolFeeExcelExtractDTO {
    private String session;
    private Department department;
    private Grade grade;
    private BigDecimal tuition;
    private BigDecimal firstTermMinPercentage;
    private BigDecimal secondTermMinPercentage;
    private BigDecimal thirdTermMinPercentage;
}
