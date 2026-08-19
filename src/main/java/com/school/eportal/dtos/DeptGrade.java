package com.school.eportal.dtos;

import com.school.eportal.data.models.enums.Department;
import com.school.eportal.data.models.enums.Grade;

public record DeptGrade(Department department, Grade grade) {}