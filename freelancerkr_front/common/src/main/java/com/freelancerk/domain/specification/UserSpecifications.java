package com.freelancerk.domain.specification;

import com.freelancerk.domain.Category;
import com.freelancerk.domain.Category_;
import com.freelancerk.domain.EscrowRefundRequest_;
import com.freelancerk.domain.User;
import com.freelancerk.domain.User_;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class UserSpecifications {

    public static Specification<User> filterForClient(final String keyword, final boolean includeLeave) {
        return (root, query, cb) -> {
            query.distinct(true);

            List<Predicate> predicates = new ArrayList<>();

            if (!includeLeave) {
                predicates.add(cb.equal(root.get(User_.leaved), false));
            }
            if (StringUtils.isNotEmpty(keyword)) {
                String newKeyword = keyword;
                if (newKeyword.startsWith("#")) {
                    newKeyword = keyword.substring(1, newKeyword.length());
                }
                List<Predicate> predicatesForKeyword = new ArrayList<>();
                predicatesForKeyword.add(cb.like(root.get(User_.name), "%" + newKeyword + "%"));
                predicatesForKeyword.add(cb.like(root.get(User_.nickname), "%" + newKeyword + "%"));
                predicatesForKeyword.add(cb.like(root.get(User_.corporateName), "%" + newKeyword + "%"));
                predicatesForKeyword.add(cb.like(root.get(User_.cellphone), "%" + newKeyword + "%"));
                predicatesForKeyword.add(cb.like(root.get(User_.email), "%" + newKeyword + "%"));
                predicates.add(cb.or(predicatesForKeyword.toArray(new Predicate[predicatesForKeyword.size()])));
            }

            predicates.add(cb.like(root.get(User_.roles), "%" + User.Role.ROLE_CLIENT.name() + "%"));

            Predicate[] predicatesArray = predicates.toArray(new Predicate[predicates.size()]);
            return cb.and(predicatesArray);
        };
    }

    public static Specification<User> filterForFreelancer(final String keyword, Long categoryId, String categoryKeyword, boolean includeLeave) {
        return (root, query, cb) -> {
            query.distinct(true);

            List<Predicate> predicates = new ArrayList<>();

            if (!includeLeave) {
                predicates.add(cb.equal(root.get(User_.leaved), false));
            }
            if (StringUtils.isNotEmpty(keyword)) {
                String newKeyword = keyword;
                if (newKeyword.startsWith("#")) {
                    newKeyword = keyword.substring(1, newKeyword.length());
                }
                List<Predicate> predicatesForKeyword = new ArrayList<>();
                predicatesForKeyword.add(cb.like(root.get(User_.name), "%" + newKeyword + "%"));
                predicatesForKeyword.add(cb.like(root.get(User_.nickname), "%" + newKeyword + "%"));
                predicatesForKeyword.add(cb.like(root.get(User_.corporateName), "%" + newKeyword + "%"));
                predicatesForKeyword.add(cb.like(root.get(User_.cellphone), "%" + newKeyword + "%"));
                predicatesForKeyword.add(cb.like(root.get(User_.email), "%" + newKeyword + "%"));
                predicates.add(cb.or(predicatesForKeyword.toArray(new Predicate[predicatesForKeyword.size()])));
            }

            if (categoryId != null) {
                predicates.add(cb.equal(root.join(User_.categories, JoinType.LEFT).get(Category_.id), categoryId));
            }

            if (StringUtils.isNotEmpty(categoryKeyword)) {
                predicates.add(cb.like(root.join(User_.categories, JoinType.LEFT).get(Category_.name), "%" + categoryKeyword + "%"));
            }

            predicates.add(cb.like(root.get(User_.roles), "%" + User.Role.ROLE_FREELANCER.name() + "%"));

            Predicate[] predicatesArray = predicates.toArray(new Predicate[predicates.size()]);
            return cb.and(predicatesArray);
        };
    }
    public static Specification<User> filterForFreelancer(final String keyword, Long categoryId, String categoryKeyword, String fpUser, boolean includeLeave) {
        return (root, query, cb) -> {
            query.distinct(true);

            List<Predicate> predicates = new ArrayList<>();

            if (!includeLeave) {
                predicates.add(cb.equal(root.get(User_.leaved), false));
            }
            if (StringUtils.isNotEmpty(keyword)) {
                String newKeyword = keyword;
                if (newKeyword.startsWith("#")) {
                    newKeyword = keyword.substring(1, newKeyword.length());
                }
                List<Predicate> predicatesForKeyword = new ArrayList<>();
                predicatesForKeyword.add(cb.like(root.get(User_.name), "%" + newKeyword + "%"));
                predicatesForKeyword.add(cb.like(root.get(User_.nickname), "%" + newKeyword + "%"));
                predicatesForKeyword.add(cb.like(root.get(User_.corporateName), "%" + newKeyword + "%"));
                predicatesForKeyword.add(cb.like(root.get(User_.cellphone), "%" + newKeyword + "%"));
                predicatesForKeyword.add(cb.like(root.get(User_.email), "%" + newKeyword + "%"));
                predicates.add(cb.or(predicatesForKeyword.toArray(new Predicate[predicatesForKeyword.size()])));
            }

            if (categoryId != null) {
                predicates.add(cb.equal(root.join(User_.categories, JoinType.LEFT).get(Category_.id), categoryId));
            }

            if (StringUtils.isNotEmpty(categoryKeyword)) {
                predicates.add(cb.like(root.join(User_.categories, JoinType.LEFT).get(Category_.name), "%" + categoryKeyword + "%"));
            }
            
            if (StringUtils.isNotEmpty(fpUser)) {
            	predicates.add(cb.like(root.get(User_.fpUser), "%" + fpUser + "%"));
            }

            predicates.add(cb.like(root.get(User_.roles), "%" + User.Role.ROLE_FREELANCER.name() + "%"));

            Predicate[] predicatesArray = predicates.toArray(new Predicate[predicates.size()]);
            return cb.and(predicatesArray);
        };
    }

    public static Specification<User> filterForCategory(Set<Category> categories) {
        return (root, query, cb) -> {
            query.distinct(true);

            List<Predicate> predicates = new ArrayList<>();

            List<Predicate> predicatesForCategory = new ArrayList<>();
            for (Category category: categories) {
                predicatesForCategory.add(cb.isMember(category, root.get(User_.categories)));
            }
            predicates.add(cb.or(predicatesForCategory.toArray(new Predicate[predicatesForCategory.size()])));


            Predicate[] predicatesArray = predicates.toArray(new Predicate[predicates.size()]);
            return cb.and(predicatesArray);
        };
    }

    public static Specification<User> filterForTopCategory(Category category) {
        return (root, query, cb) -> {
            query.distinct(true);

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.join(User_.categories, JoinType.LEFT).get(Category_.parentCategory).get(Category_.id), category.getId()));

            Predicate[] predicatesArray = predicates.toArray(new Predicate[predicates.size()]);
            return cb.and(predicatesArray);
        };
    }
}
