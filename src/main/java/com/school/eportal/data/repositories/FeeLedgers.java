package com.school.eportal.data.repositories;

import com.school.eportal.data.models.FeeLedger;
import com.school.eportal.data.models.enums.FeeLedgerStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeeLedgers extends MongoRepository<FeeLedger, String> {
   List<FeeLedger> findByStudentIdAndStatusOrStatus(String studentId, FeeLedgerStatus status, FeeLedgerStatus status1);
}
