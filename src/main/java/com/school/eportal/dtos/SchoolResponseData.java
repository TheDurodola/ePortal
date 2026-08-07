package com.school.eportal.dtos;

import com.school.eportal.data.models.enums.Department;
import com.school.eportal.data.models.enums.Grade;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@Builder
public class SchoolResponseData {
    private String session;

    private Department department;

    private Grade grade;

    private BigDecimal tuition;

    private BigDecimal total;

    private BigDecimal firstTermMinPercentage;

    private BigDecimal secondTermMinPercentage;

    private BigDecimal thirdTermMinPercentage;
}
