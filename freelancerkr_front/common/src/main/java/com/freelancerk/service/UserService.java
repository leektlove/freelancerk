package com.freelancerk.service;

import com.freelancerk.domain.User;

public interface UserService {
    void usePoint(Long userId, Integer pointUsage, String reason);

    User getById(Long userId);

    void save(User user);
    
    User getCurrentUser();

    long getPoints(long userId, User.Role loginRole);

    void deleteContents(Long sessionUserId);
}
