package com.school.eportal.security.configs;


import com.school.eportal.exceptions.AccountNotFoundException;
import com.school.eportal.security.dtos.responses.AccountResponse;
import com.school.eportal.services.interfaces.AccountService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class CustomUserDetailsServiceConfiguration implements UserDetailsService {

    private final AccountService accountService;

    @Override
    public UserDetails loadUserByUsername(@NonNull String username) {

        AccountResponse response = null;
        try {
            response = accountService.getUserAccountBy(username);
        } catch (AccountNotFoundException e) {
            throw new BadCredentialsException(e.getMessage());
        }

        List<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
            authorities.add(new SimpleGrantedAuthority(response.getRole().name()));

            return new User(response.getId(), response.getPassword(), authorities);

    }
}
