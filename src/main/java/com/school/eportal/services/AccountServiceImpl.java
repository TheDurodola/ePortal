package com.school.eportal.services;

import com.school.eportal.data.models.Account;
import com.school.eportal.data.models.enums.AccountStatus;
import com.school.eportal.data.models.enums.Role;
import com.school.eportal.data.repositories.Accounts;
import com.school.eportal.dtos.BulkAccountDto;
import com.school.eportal.dtos.requests.AddPasswordRequest;
import com.school.eportal.dtos.requests.RegisterBulkUsersRequest;
import com.school.eportal.dtos.responses.RegisterBulkUsersResponse;
import com.school.eportal.dtos.responses.addPasswordResponse;
import com.school.eportal.exceptions.AccountNotFoundException;
import com.school.eportal.exceptions.InvalidBirthDateException;
import com.school.eportal.exceptions.InvalidBulkRegistration;
import com.school.eportal.exceptions.UserNotFoundException;
import com.school.eportal.security.dtos.responses.AccountResponse;
import com.school.eportal.services.interfaces.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final Accounts accounts;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AccountResponse getUserAccountBy(String username) throws AccountNotFoundException {
        Account account = accounts.findByUsername(username)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        return AccountResponse.builder()
                .id(account.getId())
                .username(account.getUsername())
                .password(account.getPassword())
                .role(account.getRole())
                .accountStatus(account.getStatus())
                .build();
    }

    @Override
    public AccountResponse getUserAccountById(String id) throws AccountNotFoundException {
        Account account = accounts.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        return AccountResponse.builder()
                .id(account.getId())
                .username(account.getUsername())
                .password(account.getPassword())
                .role(account.getRole())
                .accountStatus(account.getStatus())
                .build();
    }


    @Override
    @Transactional
    public RegisterBulkUsersResponse bulkRegistration(RegisterBulkUsersRequest request) {
        Map<String, Map<String, List<String>>> data = new HashMap<>();
        Map<String, List<String>> passed = new HashMap<>();
        Map<String, List<String>> failed = new HashMap<>();

        List<String> failCount = new ArrayList<>();
        List<String> listOfRejectedUsernames = new ArrayList<>();
        List<String> passCount = new ArrayList<>();

        List<String> usernames = request.getData().stream()
                .map(BulkAccountDto::getUsername)
                .toList();

        List<Account> existingUsers = accounts.findAllByUsernameIn(usernames);

        if (!existingUsers.isEmpty()) {
            failCount.add(String.valueOf(existingUsers.size()));
            listOfRejectedUsernames = existingUsers.stream()
                    .map(Account::getUsername)
                    .toList();
        }

        List<String> finalListOfRejectedUsernames = listOfRejectedUsernames;
        List<Account> list = request.getData().stream()
                .filter(account -> !finalListOfRejectedUsernames.contains(account.getUsername()))
                .map(bulkAccountDto -> Account.builder()
                        .firstName(bulkAccountDto.getFirstName())
                        .username(bulkAccountDto.getUsername())
                        .lastName(bulkAccountDto.getLastName())
                        .status(AccountStatus.INACTIVE)
                        .role(bulkAccountDto.getRole())
                        .build())
                .toList();

        if (list.isEmpty()) {
            throw new InvalidBulkRegistration("All usernames are already taken.");
        }
        List<Account> accounts1 = accounts.saveAll(list);
        passCount.add(String.valueOf(accounts1.size()));

        failed.put("count", failCount);
        failed.put("usernames", listOfRejectedUsernames);
        passed.put("count", passCount);



        data.put("passed", passed);
        data.put("failed", failed);

        return RegisterBulkUsersResponse.builder()
                .data(data)
                .build();

    }

    @Override
    public addPasswordResponse addPassword(AddPasswordRequest request) {

        Account savedAccount = accounts.findByUsername(request.getUsername().toLowerCase())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!savedAccount.getBirthDate().equals(request.getDateOfBirth())){
            throw new InvalidBirthDateException("Invalid Birthdate");
        }

        savedAccount.setPassword(passwordEncoder.encode(request.getPassword()));
        savedAccount.setStatus(AccountStatus.ACTIVE);

        accounts.save(savedAccount);

        return null;
    }


}
