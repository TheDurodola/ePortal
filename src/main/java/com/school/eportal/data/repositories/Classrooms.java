package com.school.eportal.data.repositories;

import com.school.eportal.data.models.Classroom;
import com.school.eportal.data.models.enums.Department;
import com.school.eportal.data.models.enums.Division;
import com.school.eportal.data.models.enums.Grade;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface Classrooms extends MongoRepository<Classroom, String> {
     Optional<Classroom> findByGradeAndDivision(Grade grade, Division division);
     Optional<Classroom> findByStudentsContaining(String studentId);
    Optional<Classroom> findByClassTeacher(String teacherId);
}
