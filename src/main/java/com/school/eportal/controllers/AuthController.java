package com.school.eportal.controllers;

import com.school.eportal.dtos.requests.ParentRegistrationRequest;
import com.school.eportal.dtos.responses.ParentRegistrationResponse;
import com.school.eportal.services.interfaces.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/api/v1/auth")
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AccountService accountService;

    @PostMapping(value = "/registration")
    public ResponseEntity<ParentRegistrationResponse> parentRegistration(@Valid @RequestBody ParentRegistrationRequest request) {
        log.info("Parental sign up endpoint hit");
        ParentRegistrationResponse body = accountService.parentRegistration(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

}
