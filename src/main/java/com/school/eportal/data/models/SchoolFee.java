package com.school.eportal.data.models;

import com.school.eportal.data.models.enums.Department;
import com.school.eportal.data.models.enums.Grade;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.util.Date;

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

    private int firstTermMinPercentage;

    private int secondTermMinPercentage;

    private int thirdTermMinPercentage;
}
