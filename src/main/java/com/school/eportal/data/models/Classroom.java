package com.school.eportal.data.models;

import com.school.eportal.data.models.enums.Department;
import com.school.eportal.data.models.enums.Division;
import com.school.eportal.data.models.enums.Grade;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Setter
@Getter
@Document
public class Classroom {

    private String id;

    private Account classTeacher;

    private List<Account> students;

    private Department department;

    private Grade grade;

    private Division division;
}
