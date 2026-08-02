package com.school.eportal.services.interfaces;

import com.school.eportal.dtos.requests.AddPasswordRequest;
import com.school.eportal.dtos.requests.RegisterBulkUsersRequest;
import com.school.eportal.dtos.responses.RegisterBulkUsersResponse;
import com.school.eportal.dtos.responses.AddPasswordResponse;
import com.school.eportal.exceptions.AccountNotFoundException;
import com.school.eportal.security.dtos.responses.AccountResponse;



public interface AccountService {
    AccountResponse getUserAccountBy(String username) throws AccountNotFoundException;
    AccountResponse getUserAccountById(String id) throws AccountNotFoundException;
    RegisterBulkUsersResponse bulkRegistration(RegisterBulkUsersRequest request);
    AddPasswordResponse addPassword(AddPasswordRequest request);

}
