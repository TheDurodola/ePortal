package com.school.eportal.controllers;

import com.school.eportal.dtos.requests.GetProfileRequest;
import com.school.eportal.dtos.responses.GetProfileResponse;
import com.school.eportal.services.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<GetProfileResponse> profile(Authentication authentication) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getProfile(authentication));
    }

}
