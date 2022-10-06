package com.freelancerk.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.freelancerk.domain.AdminUser;
import com.freelancerk.domain.repository.AdminUserRepository;

@Service
public class AdminCustomUserDetailsService implements UserDetailsService {

    @Autowired
    AdminUserRepository adminUserRepository;

    @Override
    public AdminUser loadUserByUsername(String username) throws UsernameNotFoundException {
        return adminUserRepository.findByUsername(username);
    }
}