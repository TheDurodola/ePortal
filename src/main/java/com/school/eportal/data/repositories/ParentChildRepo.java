package com.school.eportal.data.repositories;

import com.school.eportal.data.models.ParentChild;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParentChildRepo extends MongoRepository<ParentChild, String> {

}
