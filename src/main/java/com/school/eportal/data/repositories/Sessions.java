package com.school.eportal.data.repositories;

import com.school.eportal.data.models.Session;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Sessions extends MongoRepository<Session, String> {
}
