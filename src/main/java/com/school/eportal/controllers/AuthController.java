package com.school.eportal.controllers;

import com.school.eportal.dtos.requests.AccountActivationRequest;
import com.school.eportal.dtos.requests.RegisterBulkUsersRequest;
import com.school.eportal.dtos.responses.RegisterBulkUsersResponse;
import com.school.eportal.dtos.responses.AccountActivationResponse;
import com.school.eportal.services.interfaces.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequestMapping("/api/v1/auth")
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AccountService accountService;

    @PostMapping(value = "/registration/bulk")
    public ResponseEntity<?> bulkRegistration(@Valid @RequestBody RegisterBulkUsersRequest request) {
        log.info("Sign Up Endpoint hit");
        Map<String, Object> response = new HashMap<>();
        RegisterBulkUsersResponse data = accountService.bulkRegistration(request);
        response.put("data", data);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping(value = "/account/activation")
    public ResponseEntity<?> accountActivation(@Valid @RequestBody AccountActivationRequest request) {
        Map<String, Object> response = new HashMap<>();
        AccountActivationResponse data = accountService.accountActivation(request);
        response.put("data", data);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

}
