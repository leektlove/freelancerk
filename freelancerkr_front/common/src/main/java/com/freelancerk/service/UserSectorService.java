package com.freelancerk.service;

import com.freelancerk.domain.Category;
import com.freelancerk.domain.User;

import java.util.List;
import java.util.Set;

public interface UserSectorService {

    List<Long> getUsersByKeywordMatchingMoreThan2(Set<Category> categories);

    List<User> getUsersByTopKeyword(Set<Category> categories);
}
