package com.school.eportal.security.providers;


import com.school.eportal.security.auth.CustomAuthentication;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class  CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public @Nullable Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String inputtedPassword = Objects.requireNonNull(authentication.getCredentials()).toString();

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (!passwordEncoder.matches(inputtedPassword, userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid Password");
        }

        return new CustomAuthentication(userDetails.getUsername(), true, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(@NonNull Class<?> authentication) {
        return CustomAuthentication.class==authentication;
    }
}
