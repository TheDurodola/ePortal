package com.school.eportal.data.models;

import com.school.eportal.data.models.enums.Department;
import com.school.eportal.data.models.enums.Grade;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;

@Setter
@Getter
@Builder
@Document(collection = "schoolFees")
public class SchoolFee {
    @Id
    private String id;

    private String sessionId;

    private Department department;

    private Grade grade;

    private long tuitionInKobo;

    private long total;

    private BigDecimal firstTermMinPercentage;

    private BigDecimal secondTermMinPercentage;

    private BigDecimal thirdTermMinPercentage;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
