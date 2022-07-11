package com.freelancerk.domain.specification;

import com.freelancerk.domain.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class PickMeUpSpecifications {
    public static Specification<PickMeUp> textInAllColumns(final String keyword) {
        return (root, query, cb) -> {
            query.distinct(true);
            return cb.or(
                    cb.equal(root.join(PickMeUp_.keywords, JoinType.LEFT).get(Keyword_.name), keyword)
            );
        };
    }

    public static Specification<PickMeUp> filter(Category category1st, Category category2nd, Long userId, Boolean directDeal,
                                                 Boolean hits, Boolean highQuality, Boolean certified) {
        return (root, query, cb) -> {
            query.distinct(true);
            query.orderBy(
                    cb.desc(cb.selectCase().when(cb.equal(root.get(PickMeUp_.priority), true), 2).otherwise(1)),
                    cb.desc(root.get(PickMeUp_.createdAt))
            );
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get(PickMeUp_.invalid), false));
            predicates.add(cb.equal(root.get(PickMeUp_.temp), false));
            if (category1st != null) {
                predicates.add(cb.equal(root.join(PickMeUp_.category1st).get(Category_.id), category1st.getId()));
            }
            if (category2nd != null) {
                predicates.add(cb.equal(root.join(PickMeUp_.category2nd).get(Category_.id), category2nd.getId()));
            }
            if (userId != null) {
                predicates.add(cb.equal(root.join(PickMeUp_.user).get(User_.id), userId));
            }
            predicates.add(cb.equal(root.get(PickMeUp_.invalid), false));
//            if (directDeal != null) {
//                Order order = cb.desc(cb.count(cb.equal(root.join(FreelancerPayProduct_.pickMeUp), PickMeUp_.id));
//            }

            Predicate[] predicatesArray = predicates.toArray(new Predicate[predicates.size()]);
            return cb.and(predicatesArray);
        };
    }

    public static Specification<PickMeUp> filterForFreelancerPaymentView(Long userId) {
        return (root, query, cb) -> {
            query.distinct(true);
            query.orderBy(
                    cb.desc(cb.selectCase().when(cb.equal(root.get(PickMeUp_.priority), true), 2).otherwise(1)),
                    cb.desc(root.get(PickMeUp_.createdAt))
            );
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get(PickMeUp_.invalid), false));
            predicates.add(cb.equal(root.get(PickMeUp_.temp), false));
            predicates.add(cb.equal(root.get(PickMeUp_.purchaseRecordDeleted), false));
            if (userId != null) {
                predicates.add(cb.equal(root.join(PickMeUp_.user).get(User_.id), userId));
            }

            Predicate[] predicatesArray = predicates.toArray(new Predicate[predicates.size()]);
            return cb.and(predicatesArray);
        };
    }

    public static Specification<PickMeUp> filterForAdmin(String keyword, Long category1stId, String category2ndId) {
        return (root, query, cb) -> {
            query.distinct(true);
            query.orderBy(
                    cb.desc(cb.selectCase().when(cb.equal(root.get(PickMeUp_.priority), true), 2).otherwise(1)),
                    cb.desc(root.get(PickMeUp_.createdAt))
            );
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get(PickMeUp_.invalid), false));
            predicates.add(cb.equal(root.get(PickMeUp_.temp), false));
            if (category1stId != null) {
                predicates.add(cb.equal(root.join(PickMeUp_.category1st).get(Category_.id), category1stId));
            }
            if (category2ndId != null) {
                predicates.add(cb.equal(root.join(PickMeUp_.category2nd).get(Category_.id), category2ndId));
            }
            if (StringUtils.isNotEmpty(keyword)) {
                String newKeyword = keyword;
                if (newKeyword.startsWith("#")) {
                    newKeyword = keyword.substring(1, newKeyword.length());
                }
                List<Predicate> predicatesForKeyword = new ArrayList<>();
                predicatesForKeyword.add(cb.like(root.get(PickMeUp_.user).get(User_.nickname), "%" + newKeyword + "%"));
                predicatesForKeyword.add(cb.like(root.get(PickMeUp_.user).get(User_.name), "%" + newKeyword + "%"));
                predicatesForKeyword.add(cb.like(root.get(PickMeUp_.user).get(User_.email), "%" + newKeyword + "%"));
                predicatesForKeyword.add(cb.like(root.get(PickMeUp_.title), "%" + newKeyword + "%"));
                predicatesForKeyword.add(cb.like(root.get(PickMeUp_.description), "%" + newKeyword + "%"));
                predicates.add(cb.or(predicatesForKeyword.toArray(new Predicate[predicatesForKeyword.size()])));
            }
            Predicate[] predicatesArray = predicates.toArray(new Predicate[predicates.size()]);
            return cb.and(predicatesArray);
        };
    }
}
