package com.school.eportal.controllers;

import com.school.eportal.dtos.requests.RegisterUserRequest;
import com.school.eportal.dtos.requests.SignUpRequest;
import com.school.eportal.services.interfaces.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequestMapping("/api/auth")
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AccountService accountService;

    @PostMapping(value = "/signup")
    public ResponseEntity<?> createUser( @RequestBody SignUpRequest request) {
        log.info("Sign Up Endpoint hit");
        Map<String, Object> response = new HashMap<>();
        response.put("message", "User account created successfully");
        response.put("data", accountService.signUp(request));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

}
