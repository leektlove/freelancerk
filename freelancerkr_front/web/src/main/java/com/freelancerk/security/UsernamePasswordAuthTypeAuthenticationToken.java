package com.freelancerk.security;

import java.util.Collection;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class UsernamePasswordAuthTypeAuthenticationToken extends UsernamePasswordAuthenticationToken {
    private String authType;
    private String role;
    private String fpUser;

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
    
    public UsernamePasswordAuthTypeAuthenticationToken(Object principal, Object credentials, String authType, String role, String fpUser) {
        super(principal, credentials);
        this.authType = authType;
        this.role = role;
        this.fpUser = fpUser;
    }

    public String getAuthType() {
        return authType;
    }

    public String getRole() {
        return role;
    }
    
    public String getFpUser() {
    	return fpUser;
    }
}
