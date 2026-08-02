package com.school.eportal.services.interfaces;

import com.school.eportal.dtos.requests.AccountActivationRequest;
import com.school.eportal.dtos.requests.ParentRegistrationRequest;
import com.school.eportal.dtos.requests.RegisterBulkUsersRequest;
import com.school.eportal.dtos.responses.ParentRegistrationResponse;
import com.school.eportal.dtos.responses.PreRegistrationResponse;
import com.school.eportal.dtos.responses.RegisterBulkUsersResponse;
import com.school.eportal.dtos.responses.AccountActivationResponse;
import com.school.eportal.exceptions.AccountNotFoundException;
import com.school.eportal.security.dtos.responses.AccountResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.web.multipart.MultipartFile;


public interface AccountService {
    AccountResponse getUserAccountBy(String username) throws AccountNotFoundException;
    AccountResponse getUserAccountById(String id) throws AccountNotFoundException;
    RegisterBulkUsersResponse bulkRegistration(RegisterBulkUsersRequest request);
    AccountActivationResponse accountActivation(AccountActivationRequest request);
    PreRegistrationResponse preRegistration(@NonNull MultipartFile request);
    ParentRegistrationResponse parentRegistration(ParentRegistrationRequest request);
}
