package com.freelancerk.service;

import com.freelancerk.domain.User;
import com.freelancerk.exception.NotLoggedInException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

public class BaseService {

    protected Long getSessionUserId() {
        if (SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken) {
            throw NotLoggedInException.getInstance();
        }
        Long userId = ((User) SecurityContextHolder.getContext().getAuthentication()).getDetails().getId();
        return userId;
    }

    protected boolean isLoggedIn() {
        return  SecurityContextHolder.getContext().getAuthentication() != null &&
                SecurityContextHolder.getContext().getAuthentication().isAuthenticated() &&
                //when Anonymous Authentication is enabled
                !(SecurityContextHolder.getContext().getAuthentication()
                        instanceof AnonymousAuthenticationToken);
    }

    protected boolean isLoggedIsAsClient() {
        return ((User) SecurityContextHolder.getContext().getAuthentication()).isLoginAsClient();
    }
}
