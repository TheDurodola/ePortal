package com.school.eportal.security.filters;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.*;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.school.eportal.data.models.enums.AccountStatus;
import com.school.eportal.data.models.enums.Role;
import com.school.eportal.exceptions.AccountNotFoundException;
import com.school.eportal.security.dtos.responses.AccountResponse;
import com.school.eportal.services.interfaces.AccountService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthorizationFilter extends OncePerRequestFilter {

    private final AccountService accountService;
    private final ObjectMapper objectMapper;

    @Value("${jwt.signing.key}")
    private String secret;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        log.info("Authorization Filter Reached");
        try {
            if (isPublicApi(request)) {
                log.info("Request Path: {}", request.getServletPath());
                log.debug("Request is for a public API");
                filterChain.doFilter(request, response);
                return;
            }

            String authorizationHeader = request.getHeader(AUTHORIZATION);
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                log.debug("Authorization header: {}", authorizationHeader);
                createErrorResponse(response);
            } else {
                String jwt = authorizationHeader.split(" ")[1];

                Algorithm algorithm = Algorithm.HMAC256(secret.getBytes());
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(jwt);
                String id = decodedJWT.getSubject();
                log.debug("USER ID: {}", id);


                AccountResponse userAccountBy = accountService.getUserAccountById(id);

                Role role = userAccountBy.getRole();
                AccountStatus accountStatus = userAccountBy.getAccountStatus();
                List<SimpleGrantedAuthority> accountRole = new ArrayList<>();
                accountRole.add(new SimpleGrantedAuthority(role.name()));
                accountRole.add(new SimpleGrantedAuthority(accountStatus.name()));
                Authentication auth = new UsernamePasswordAuthenticationToken(userAccountBy.getId(), null, accountRole);
                SecurityContextHolder.getContext().setAuthentication(auth);
                log.info("Authorization Completed");
                filterChain.doFilter(request, response);
            }

        } catch (IOException | IllegalArgumentException e) {
            log.info("Error processing request", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            createErrorResponse(response);
        } catch (JWTDecodeException | TokenExpiredException | MissingClaimException
                 | AlgorithmMismatchException | SignatureVerificationException e){
            log.info("Error processing request", e);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            createErrorResponse(response);
        } catch (AccountNotFoundException e) {
            log.info("Critical: Error processing request", e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            createErrorResponse(response);
        }
    }

    private void createErrorResponse(HttpServletResponse response) throws IOException {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Invalid JWT");
        response.setContentType("application/json");
        response.getOutputStream().write(objectMapper.writeValueAsBytes(error));
        response.flushBuffer();
    }

    private static boolean isPublicApi(HttpServletRequest request) {
        return request.getServletPath().equals("/api/v1/auth/signin")
                || request.getServletPath().equals("/api/auth/signup")
                || request.getServletPath().equals("/test/live");
    }
}
