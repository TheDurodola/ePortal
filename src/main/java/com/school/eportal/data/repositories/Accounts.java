package com.school.eportal.data.repositories;

import com.school.eportal.data.models.Account;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface Accounts extends MongoRepository<Account, String> {
    Optional<Account> findByUsername(String username);
    List<Account> findAllByUsernameIn(List<String> usernames);
}
