package com.school.eportal.data.models;

import com.school.eportal.data.models.enums.Department;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;


@Setter
@Getter
@Builder
@Document
public class DepartmentPath {
    private String id;
    private Department department;
    private List<Account> students;

    public void addStudent(Account account) {
        students.add(account);
    }
    public void removeStudent(Account account) {
        students.remove(account);
    }

}
