package com.school.eportal.security.auth;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;


public class CustomAuthentication implements Authentication {
    private final String username;
    private String password;
    private boolean authenticated;
    private Collection<? extends GrantedAuthority> authorities;

    public CustomAuthentication(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public CustomAuthentication(String username, boolean authenticated, Collection<? extends GrantedAuthority> authorities) {
        this.username = username;
        this.authenticated = authenticated;
        this.authorities = authorities;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public @Nullable Object getCredentials() {
        return password;
    }

    @Override
    public @Nullable Object getDetails() {
        return null;
    }

    @Override
    public @Nullable Object getPrincipal() {
        return username;
    }

    @Override
    public boolean isAuthenticated() {
        return !authorities.isEmpty();
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = isAuthenticated;
    }

    @Override
    public boolean equals(Object another) {
        return false;
    }

    @Override
    public String toString() {
        return "";
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public String getName() {
        return username;
    }
}
