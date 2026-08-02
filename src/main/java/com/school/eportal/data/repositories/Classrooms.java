package com.school.eportal.data.repositories;

import com.school.eportal.data.models.Classroom;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Classrooms extends MongoRepository<Classroom, String> {
}
