package com.school.eportal.controllers;

import com.school.eportal.dtos.requests.PaySchoolFeesRequest;
import com.school.eportal.dtos.requests.PaystackWebhookRequest;
import com.school.eportal.dtos.requests.VerifySchoolFeesPaymentRequest;
import com.school.eportal.dtos.responses.CreateSchoolFeesResponse;
import com.school.eportal.dtos.responses.GetSchoolFeesDetailsResponse;
import com.school.eportal.dtos.responses.PaySchoolFeesResponse;
import com.school.eportal.dtos.responses.VerifySchoolFeesPaymentResponse;
import com.school.eportal.services.interfaces.SchoolFeeService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/schoolfee")
public class SchoolFeeController {
    private SchoolFeeService schoolFeeService;

    @PostMapping(value = "/excel", consumes = "multipart/form-data")
    public ResponseEntity<CreateSchoolFeesResponse> uploadNewSchoolFees(@RequestParam("file") @Valid MultipartFile file) {
        log.info("Uploading excel file {} for school fees creation.", file.getOriginalFilename());
        CreateSchoolFeesResponse response = schoolFeeService.createSchoolFees(file);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    @PostMapping(value = "/payment")
    public ResponseEntity<PaySchoolFeesResponse> paySchoolFee(@RequestBody PaySchoolFeesRequest request, Authentication authentication) {

        PaySchoolFeesResponse response = schoolFeeService.paySchoolFees(request, authentication);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    @PostMapping(value = "/webhook")
    public ResponseEntity<?> updatePayment(@RequestBody PaystackWebhookRequest request) {
        schoolFeeService.updateTransaction(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @GetMapping(value = "/verification")
    public ResponseEntity<VerifySchoolFeesPaymentResponse> verifySchoolFeesDetails(@RequestBody VerifySchoolFeesPaymentRequest request) {

        VerifySchoolFeesPaymentResponse response = schoolFeeService.verifySchoolFees(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping()
    public ResponseEntity<GetSchoolFeesDetailsResponse> getSchoolFeesDetails(Authentication authentication) {
        GetSchoolFeesDetailsResponse response = schoolFeeService.getSchoolFeesDetails(authentication);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


}
