package com.school.eportal.data.repositories;

import com.school.eportal.data.models.DepartmentPath;
import com.school.eportal.data.models.enums.Department;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface DepartmentPathRepo extends MongoRepository<DepartmentPath,String> {
    Optional<DepartmentPath> findFirstByDepartment(Department department);
    Optional<DepartmentPath> findByStudentsContaining(String studentId);

    List<DepartmentPath> findByStudentsIn(Set<String> targetStudentIds);
}
