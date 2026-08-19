package com.school.eportal.dtos;

import com.school.eportal.data.models.enums.Department;
import com.school.eportal.data.models.enums.Grade;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class SchoolFeesDetailPayload {
    private String session;
    private String studentID;
    private String studentFirstName;
    private String studentLastName;
    private Grade grade;
    private Department department;
    private BigDecimal tuition;
    private BigDecimal total;
    private BigDecimal totalPaid;
}
