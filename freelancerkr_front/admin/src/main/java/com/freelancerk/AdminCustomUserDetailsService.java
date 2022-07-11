package com.freelancerk;

import com.freelancerk.domain.AdminUser;
import com.freelancerk.domain.repository.AdminUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AdminCustomUserDetailsService implements UserDetailsService {

    @Autowired
    AdminUserRepository adminUserRepository;

    @Override
    public AdminUser loadUserByUsername(String username) throws UsernameNotFoundException {
        return adminUserRepository.findByUsername(username);
    }
}