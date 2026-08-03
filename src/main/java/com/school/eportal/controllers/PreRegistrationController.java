package com.school.eportal.controllers;

import com.school.eportal.dtos.requests.AccountActivationRequest;
import com.school.eportal.dtos.responses.AccountActivationResponse;
import com.school.eportal.dtos.responses.PreRegistrationResponse;
import com.school.eportal.services.interfaces.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/preregistration")
public class PreRegistrationController {
    private final AccountService accountService;

    @PostMapping(value = "/excel", consumes = "multipart/form-data")
    public ResponseEntity<PreRegistrationResponse> uploadExcelFile(@RequestParam("file") @Valid MultipartFile file) {
        log.info("Pre-Registration: Uploading excel file {} ", file.getOriginalFilename());

        PreRegistrationResponse body = accountService.preRegistration(file);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(body);
    }

    @PatchMapping(value = "/activation")
    public ResponseEntity<AccountActivationResponse> accountActivation(@Valid @RequestBody AccountActivationRequest request) {
        log.info("Account activation endpoint hit");

        AccountActivationResponse body = accountService.accountActivation(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(body);
    }

}
