package com.school.eportal.data.repositories;

import com.school.eportal.data.models.FeeTransaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeeTransactions extends MongoRepository<FeeTransaction, String> {
    List<FeeTransaction> findByFeeLedger(String feeLedger);

    Optional<FeeTransaction> findByPaymentReference(String s);

}
