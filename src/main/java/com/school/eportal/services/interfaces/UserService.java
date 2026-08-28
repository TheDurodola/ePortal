package com.school.eportal.services.interfaces;

import com.school.eportal.dtos.requests.GetProfileRequest;
import com.school.eportal.dtos.responses.GetProfileResponse;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

public interface UserService {
    GetProfileResponse getProfile(Authentication authentication);
}

