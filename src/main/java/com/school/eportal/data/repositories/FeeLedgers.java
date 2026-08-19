package com.school.eportal.data.repositories;

import com.school.eportal.data.models.FeeLedger;
import com.school.eportal.data.models.enums.FeeLedgerStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeeLedgers extends MongoRepository<FeeLedger, String> {
   List<FeeLedger> findByStudentIdAndStatusOrStatus(String studentId, FeeLedgerStatus status, FeeLedgerStatus status1);
   Optional<FeeLedger> findByStudentIdAndAcademicSessionId(String studentId, String academicSessionId);
   List<FeeLedger> findByStudentIdInAndStatusIn(List<String> studentIds, List<FeeLedgerStatus> statuses);
}
