package com.school.eportal.security.configs;


import com.school.eportal.security.filters.CustomAuthenticationFilter;
import com.school.eportal.security.filters.CustomAuthorizationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorityAuthorizationManager;
import org.springframework.security.authorization.AuthorizationManagers;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityFilterChainConfiguration {

    private final CustomAuthenticationFilter customAuthenticationFilter;
    private final CustomAuthorizationFilter customAuthorizationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterAt(customAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(customAuthorizationFilter, CustomAuthenticationFilter.class)
                .authorizeHttpRequests((c) -> c
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/preregistration/activation").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/registration").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/preregistration/excel")
                        .access(AuthorizationManagers.allOf(
                                AuthorityAuthorizationManager.hasAuthority("ACTIVE"),
                                AuthorityAuthorizationManager.hasAnyAuthority("PRINCIPAL", "ADMIN")
                        ))
                        .requestMatchers(HttpMethod.GET, "/api/v1/profile").hasAuthority("ACTIVE")
                        .requestMatchers(HttpMethod.POST, "/api/v1/schoolfee/excel")
                        .access(AuthorizationManagers.allOf(
                                AuthorityAuthorizationManager.hasAuthority("ACTIVE"),
                                AuthorityAuthorizationManager.hasAnyAuthority("PRINCIPAL", "ADMIN")
                        ))
                        .requestMatchers(HttpMethod.POST, "/api/v1/schoolfee/payment").hasAllAuthorities("ACTIVE", "PARENT")
                        .requestMatchers(HttpMethod.GET, "/api/v1/schoolfee").hasAllAuthorities("ACTIVE", "PARENT")
                        .requestMatchers(HttpMethod.GET, "/api/v1/schoolfee/verification").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/schoolfee/webhook").permitAll()
                        .anyRequest().hasAuthority("ACTIVE"))
                .build();
    }
}
