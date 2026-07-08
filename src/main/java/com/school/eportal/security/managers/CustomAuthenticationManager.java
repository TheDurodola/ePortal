package com.school.eportal.security.managers;

import com.school.eportal.security.exceptions.AuthenticationNotSupportedException;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class CustomAuthenticationManager implements AuthenticationManager {

    private final AuthenticationProvider authenticationProvider;

    @Override
    public Authentication authenticate(@NonNull Authentication authentication) throws AuthenticationException {

        if (authenticationProvider.supports(authentication.getClass())) {
            return authenticationProvider.authenticate(authentication);
        }
        throw new AuthenticationNotSupportedException("Bad credentials");
    }
}
