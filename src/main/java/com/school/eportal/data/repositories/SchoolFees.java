package com.school.eportal.data.repositories;


import com.school.eportal.data.models.SchoolFee;
import com.school.eportal.data.models.enums.Department;
import com.school.eportal.data.models.enums.Grade;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface SchoolFees extends MongoRepository<SchoolFee, String> {
    Optional<SchoolFee> findBySessionIdAndDepartmentAndGrade(String sessionId, Department department, Grade grade);

    Collection<Object> findBySessionIdAndDepartmentInAndGradeIn(String id, List<Department> list, List<Grade> list1);
}
