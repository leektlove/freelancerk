package com.freelancerk.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.freelancerk.domain.User;
import com.freelancerk.domain.repository.UserRepository;
import com.freelancerk.exception.UsernameNotFoundException;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepo;

    private final AccountStatusUserDetailsChecker detailsChecker = new AccountStatusUserDetailsChecker();

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        final User user = convert(userRepo.findByUsername(username));
        detailsChecker.check(user);
        return user;
    }
    
    public User loadUserByUsernameAndFpUser(String username,String fpUser) throws UsernameNotFoundException {
        final User user = convert(userRepo.findByUsernameAndFpUser(username,fpUser));
        detailsChecker.check(user);
        return user;
    }

    private User convert(User user) {
        if (user == null) {
            throw UsernameNotFoundException.getInstance();
        }
        return user;
    }

    public void increaseLoginFailCount(Long id) {
        userRepo.updatePasswordFailCnt(id);
    }

    public void resetPasswordFailCount(Long id) {
        userRepo.resetPasswordFailCnt(id);
    }
}
