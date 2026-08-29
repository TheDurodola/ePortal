package com.school.eportal.services.interfaces;

import com.school.eportal.dtos.responses.GetProfileResponse;
import org.springframework.security.core.Authentication;

public interface UserService {
    GetProfileResponse getProfile(Authentication authentication);
}

