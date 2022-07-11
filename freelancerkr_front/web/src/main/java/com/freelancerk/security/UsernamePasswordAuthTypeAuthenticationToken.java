package com.freelancerk.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class UsernamePasswordAuthTypeAuthenticationToken extends UsernamePasswordAuthenticationToken {
    private String authType;
    private String role;

    public UsernamePasswordAuthTypeAuthenticationToken(Object principal, Object credentials) {
        super(principal, credentials);
    }

    public UsernamePasswordAuthTypeAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
    }

    public UsernamePasswordAuthTypeAuthenticationToken(Object principal, Object credentials, String authType, String role) {
        super(principal, credentials);
        this.authType = authType;
        this.role = role;
    }

    public String getAuthType() {
        return authType;
    }

    public String getRole() {
        return role;
    }
}
