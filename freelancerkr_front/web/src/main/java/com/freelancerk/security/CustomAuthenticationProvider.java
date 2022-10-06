package com.freelancerk.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.freelancerk.domain.User;
import com.freelancerk.exception.UsernameNotFoundException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private CustomUserDetailsService service;
    private PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UsernamePasswordAuthTypeAuthenticationToken authenticationToken = (UsernamePasswordAuthTypeAuthenticationToken) authentication;
        String principal = null;												
        String credential = null;
        String role = authenticationToken.getRole();
        String fpUser = authenticationToken.getFpUser();
        User user = new User();
        
        if(fpUser!=null && !"".equals(fpUser)) {
        	user = service.loadUserByUsernameAndFpUser(authenticationToken.getName(),authenticationToken.getFpUser());
		}else {
			user = service.loadUserByUsername(authenticationToken.getName());
		}
        
        if (!user.getRoles().contains(role)) {
            throw UsernameNotFoundException.getInstance();
        }

        if (User.AuthType.EMAIL.name().equalsIgnoreCase(authenticationToken.getAuthType())) {

            principal = user.getUsername();
            credential = user.getPassword();
            boolean match = passwordEncoder.matches((CharSequence) authenticationToken.getCredentials(), credential);
            if (!match) {
                service.increaseLoginFailCount(user.getId());
                throw new BadCredentialsException("email password not match");
            }
        } else if (User.AuthType.FACEBOOK.name().equalsIgnoreCase(authenticationToken.getAuthType()) ||
                User.AuthType.KAKAO.name().equalsIgnoreCase(authenticationToken.getAuthType()) ||
                User.AuthType.NAVER.name().equalsIgnoreCase(authenticationToken.getAuthType())) {
            principal = user.getUsername();
            credential = user.getPassword();
        }
        service.resetPasswordFailCount(user.getId());
        return new UsernamePasswordAuthenticationToken(principal, credential);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication == UsernamePasswordAuthTypeAuthenticationToken.class;
    }

    void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
}
