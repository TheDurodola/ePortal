package com.school.eportal.services;

import com.school.eportal.data.models.Account;
import com.school.eportal.data.models.enums.AccountStatus;
import com.school.eportal.data.models.enums.Role;
import com.school.eportal.data.repositories.Accounts;
import com.school.eportal.dtos.requests.SignUpRequest;
import com.school.eportal.dtos.responses.SignUpResponse;
import com.school.eportal.exceptions.AccountNotFoundException;
import com.school.eportal.security.dtos.responses.AccountResponse;
import com.school.eportal.services.interfaces.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
    public SignUpResponse signUp(SignUpRequest signUpRequest) {
        Account account = new Account();
        account.setStatus(AccountStatus.ACTIVE);
        account.setUsername("lord_boj");
        account.setPassword(passwordEncoder.encode("lord_boj"));
        account.setFirstName("Lord");
        account.setLastName("Boj");
        account.setRole(Role.ACCOUNTANT);

        accounts.save(account);
        return null;
    }

}
