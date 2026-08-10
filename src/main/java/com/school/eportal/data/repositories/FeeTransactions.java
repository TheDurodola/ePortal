package com.school.eportal.data.repositories;

import com.school.eportal.data.models.FeeTransaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeeTransactions extends MongoRepository<FeeTransaction, String> {
    List<FeeTransaction> findByFeeLedger(String feeLedger);
}
