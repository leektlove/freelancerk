package com.freelancerk.service.impl;

import com.freelancerk.domain.Category;
import com.freelancerk.domain.User;
import com.freelancerk.domain.repository.CategoryRepository;
import com.freelancerk.domain.repository.UserRepository;
import com.freelancerk.domain.specification.UserSpecifications;
import com.freelancerk.service.UserSectorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserSectorServiceImpl implements UserSectorService {

    private UserRepository userRepository;
    private CategoryRepository categoryRepository;
    Map<Long, Set<Category>> userCategoryMap = new LinkedHashMap<>();
    Set<Long> translateCategoryIds = new HashSet<>();
    Environment env;

    @Autowired
    public UserSectorServiceImpl(UserRepository userRepository, CategoryRepository categoryRepository, Environment env) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.env = env;
        if (env.getActiveProfiles().length > 0 && "real".equalsIgnoreCase(env.getActiveProfiles()[0])) {
            fillCategoryMap();
        }
    }

    @Override
    public List<Long> getUsersByKeywordMatchingMoreThan2(Set<Category> projectCategories) {

        if (projectCategories == null || projectCategories.size() < 2) return new ArrayList<>();
        Set<Long> userIds = new HashSet<>();
        Iterator<Long> iterator = userCategoryMap.keySet().iterator();
        boolean projectContainsLessonSector = isContainsLessonSector(projectCategories);
        while (iterator.hasNext()) {
            Long key = iterator.next();
            int matched = 0;
            boolean userContainLessonSector = false;
            boolean userContainTranslateSector = false;
            for (Category category: userCategoryMap.get(key)) {
                if (isContainTranslateCategory(projectCategories)) {
                    if (isContainTargetTranslateCategory(category, projectCategories)) {
                        userContainTranslateSector = true;
                    }
                } else {
                    if (projectCategories.stream().map(Category::getName).collect(Collectors.toSet()).contains(category.getName())) {
                        matched++;
                    }
                    if (category.getParentCategory() != null && category.getParentCategory().getParentCategory() != null &&
                            category.getParentCategory().getParentCategory().getId() == 168) {
                        userContainLessonSector = true;
                    }
                }
            }
            if (matched >= 2) {
                log.debug("<<< categories: {}, userId: {}", userCategoryMap.get(key), key);
                userIds.add(key);
                continue;
            }

            if (projectContainsLessonSector && userContainLessonSector) {
                userIds.add(key);
            }

            if (userContainTranslateSector) {
                userIds.add(key);
            }
        }

        return new ArrayList<>(userIds);
    }

    @Override
    public List<User> getUsersByTopKeyword(Set<Category> categories) {
        Set<User> users = new HashSet<>();
        for (Category category: categories) {
            users.addAll(userRepository.findAll(UserSpecifications.filterForTopCategory(category)));
        }
        return new ArrayList<>(users);
    }

    private void fillCategoryMap() {
        for (User user: userRepository.findByLeavedFalse()) {
            userCategoryMap.put(user.getId(), user.getCategories());
        }

        translateCategoryIds = categoryRepository.findByParentCategoryId(168L).stream().map(Category::getId).collect(Collectors.toSet());
    }

    private boolean isContainsLessonSector(Set<Category> categories) {
        for (Category category: categories) {
            try {
                if (category.getParentCategory() != null && category.getParentCategory().getParentCategory() != null &&
                category.getParentCategory().getParentCategory().getId() == 168) {
                    return true;
                }
            } catch (Exception e){
                log.error("<<< error at isContainsLessonSector. categories: {}", categories, e);
            }
        }
        return false;
    }

    private boolean isContainTranslateCategory(Set<Category> topCategorySet) {
        try {
            return topCategorySet.stream()
                            .map(Category::getParentCategory).map(Category::getParentCategory).map(Category::getId).
                            collect(Collectors.toSet()).contains(94L);
        } catch (Exception e) {
            log.error("<<< error at isContainTranslateCategory. topCategories: {}", topCategorySet, e);
        }

        return false;
    }

    private boolean isContainTargetTranslateCategory(Category category, Set<Category> topCategorySet) {
        try {
            return topCategorySet.stream()
                    .map(Category::getParentCategory).map(Category::getId).collect(Collectors.toSet()).contains(category.getParentCategory().getId()) &&
                    topCategorySet.stream()
                    .map(Category::getParentCategory).map(Category::getParentCategory).map(Category::getId).
                            collect(Collectors.toSet()).contains(94L);
        } catch (Exception e) {
            log.error("<<< error at isContainTranslateCategory. topCategories: {}", topCategorySet, e);
        }

        return false;
    }
}
