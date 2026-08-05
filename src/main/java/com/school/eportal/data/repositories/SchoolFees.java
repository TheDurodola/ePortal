package com.school.eportal.data.repositories;


import com.school.eportal.data.models.SchoolFee;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SchoolFees extends MongoRepository<SchoolFee, String> {
}
