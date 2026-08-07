package com.school.eportal.data.repositories;

import com.school.eportal.data.models.Session;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface Sessions extends MongoRepository<Session, String> {
    Optional<Session> findByStartYear(int startYear);
    Optional<Session> findById(String id);
    Optional<Session> findByIsCurrentTrue();
}
