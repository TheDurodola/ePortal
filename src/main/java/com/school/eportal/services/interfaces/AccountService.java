package com.school.eportal.services.interfaces;

import com.school.eportal.dtos.requests.SignUpRequest;
import com.school.eportal.dtos.responses.SignUpResponse;
import com.school.eportal.exceptions.AccountNotFoundException;
import com.school.eportal.security.dtos.responses.AccountResponse;



public interface AccountService {
    AccountResponse getUserAccountBy(String username) throws AccountNotFoundException;
    AccountResponse getUserAccountById(String id) throws AccountNotFoundException;
    SignUpResponse signUp(SignUpRequest signUpRequest);
}
