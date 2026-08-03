package com.school.eportal.data.models;

import com.school.eportal.data.models.enums.Department;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "departmentPaths")
public class DepartmentPath {

    @Id
    private String id;

    private Department department;

    @Builder.Default
    private List<String> students = new ArrayList<>();

    public void addStudent(Account account) {
        if (students == null) {
            students = new ArrayList<>();
        }
        students.add(account.getId());
    }

    public void removeStudent(Account account) {
        if (students != null) {
            students.remove(account.getId());
        }
    }
}