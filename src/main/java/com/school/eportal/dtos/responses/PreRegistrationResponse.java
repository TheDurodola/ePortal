package com.school.eportal.dtos.responses;

import com.school.eportal.dtos.StudentExcelDTOResponseBody;
import com.school.eportal.dtos.TeacherExcelDTOResponseBody;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Builder
public class PreRegistrationResponse {
    List<StudentExcelDTOResponseBody> students;
    List<TeacherExcelDTOResponseBody> teachers;
}
