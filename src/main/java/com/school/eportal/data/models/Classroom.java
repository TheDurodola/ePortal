package com.school.eportal.data.models;

import com.school.eportal.data.models.enums.Department;
import com.school.eportal.data.models.enums.Division;
import com.school.eportal.data.models.enums.Grade;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Document(collection = "classrooms")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Classroom {

    @Id
    private String id;

    @Indexed(unique = true, sparse = true)
    private String classTeacher;

    @Indexed
    @Builder.Default
    private List<String> students = new ArrayList<>();

    @NotNull
    private Grade grade;

    @NotNull
    private Division division;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    public void addStudent(String studentId) {
        students.add(studentId);
}
}