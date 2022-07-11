package com.freelancerk.service;

import com.freelancerk.domain.User;

public interface ResettingPasswordService {

    void resetPassword(User user, String email);
}
