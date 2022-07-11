package com.freelancerk.domain.specification;

import com.freelancerk.domain.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PickMeUpTicketSpecifications {

    public static Predicate[] getPredicate(CriteriaQuery query, CriteriaBuilder cb, Root<PickMeUpTicket> root,
                                           Category category1st, Category category2nd, String keyword, Long userId, boolean onlyDirectMarketAvailable) {

            query.distinct(true);
            query.orderBy(
                    cb.desc(cb.selectCase().when(cb.equal(root.join(PickMeUpTicket_.pickMeUp).get(PickMeUp_.priority), true), 2).otherwise(1)),
                    cb.desc(root.get(PickMeUpTicket_.pickMeUp).get(PickMeUp_.createdAt))
            );
            query.multiselect(root.get(PickMeUpTicket_.pickMeUp).get(PickMeUp_.id));

            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get(PickMeUpTicket_.invalid), false));
            predicates.add(cb.equal(root.join(PickMeUpTicket_.pickMeUp).get(PickMeUp_.invalid), false));
            predicates.add(cb.equal(root.join(PickMeUpTicket_.pickMeUp).get(PickMeUp_.temp), false));
            predicates.add(cb.greaterThanOrEqualTo(root.get(PickMeUpTicket_.endAt), LocalDateTime.now()));
            predicates.add(cb.lessThanOrEqualTo(root.get(PickMeUpTicket_.startAt), LocalDateTime.now()));

            if (StringUtils.isEmpty(keyword) && category1st != null) {
                predicates.add(cb.equal(root.join(PickMeUpTicket_.pickMeUp).get(PickMeUp_.category1st).get(Category_.id), category1st.getId()));
            }
            if (StringUtils.isEmpty(keyword) && category2nd != null) {
                predicates.add(cb.equal(root.join(PickMeUpTicket_.pickMeUp).get(PickMeUp_.category2nd).get(Category_.id), category2nd.getId()));
            }
            if (StringUtils.isNotEmpty(keyword)) {
                List<Predicate> predicatesForKeyword = new ArrayList<>();
                predicatesForKeyword.add(cb.like(root.join(PickMeUpTicket_.pickMeUp).get(PickMeUp_.title), "%" + keyword + "%"));
                predicatesForKeyword.add(cb.like(root.join(PickMeUpTicket_.pickMeUp).get(PickMeUp_.description), "%" + keyword + "%"));
                predicatesForKeyword.add(cb.equal(root.join(PickMeUpTicket_.pickMeUp).get(PickMeUp_.category1st).get(Category_.name), keyword));
                predicatesForKeyword.add(cb.equal(root.join(PickMeUpTicket_.pickMeUp).join(PickMeUp_.user).join(User_.categories, JoinType.LEFT).get(Category_.name), keyword));
                predicates.add(cb.or(predicatesForKeyword.toArray(new Predicate[predicatesForKeyword.size()])));
            }
            if (userId != null) {
                predicates.add(cb.equal(root.join(PickMeUpTicket_.pickMeUp).get(PickMeUp_.user).get(User_.id), userId));
            }
            if (onlyDirectMarketAvailable) {
                predicates.add(cb.equal(root.join(PickMeUpTicket_.freelancerProductItemType).get(FreelancerProductItemType_.code), FreelancerProductItemType.Code.DIRECT_DEAL));
            } else {
                predicates.add(cb.equal(root.join(PickMeUpTicket_.freelancerProductItemType).get(FreelancerProductItemType_.code), FreelancerProductItemType.Code.PICK_ME_UP));
            }

            query.groupBy(root.get(PickMeUpTicket_.pickMeUp));

            Predicate[] predicatesArray = predicates.toArray(new Predicate[predicates.size()]);
            return predicatesArray;
    }

    public static Specification<PickMeUpTicket> filter(Category category1st, Category category2nd, String keyword, Long userId, boolean onlyDirectMarketAvailable) {

        return ((root, query, cb) -> {
            query.distinct(true);
            query.orderBy(
                    cb.desc(cb.selectCase().when(cb.equal(root.join(PickMeUpTicket_.pickMeUp).get(PickMeUp_.priority), true), 2).otherwise(1)),
                    cb.desc(root.get(PickMeUpTicket_.pickMeUp).get(PickMeUp_.createdAt))
            );
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get(PickMeUpTicket_.invalid), false));
            predicates.add(cb.equal(root.join(PickMeUpTicket_.pickMeUp).get(PickMeUp_.invalid), false));
            predicates.add(cb.equal(root.join(PickMeUpTicket_.pickMeUp).get(PickMeUp_.temp), false));
            predicates.add(cb.greaterThanOrEqualTo(root.get(PickMeUpTicket_.endAt), LocalDateTime.now()));
            predicates.add(cb.lessThanOrEqualTo(root.get(PickMeUpTicket_.startAt), LocalDateTime.now()));

            if (StringUtils.isEmpty(keyword) && category1st != null) {
                predicates.add(cb.equal(root.join(PickMeUpTicket_.pickMeUp).get(PickMeUp_.category1st).get(Category_.id), category1st.getId()));
            }
            if (StringUtils.isEmpty(keyword) && category2nd != null) {
                predicates.add(cb.equal(root.join(PickMeUpTicket_.pickMeUp).get(PickMeUp_.category2nd).get(Category_.id), category2nd.getId()));
            }
            if (StringUtils.isNotEmpty(keyword)) {
                List<Predicate> predicatesForKeyword = new ArrayList<>();
                predicatesForKeyword.add(cb.like(root.join(PickMeUpTicket_.pickMeUp).get(PickMeUp_.title), "%" + keyword + "%"));
                predicatesForKeyword.add(cb.like(root.join(PickMeUpTicket_.pickMeUp).get(PickMeUp_.description), "%" + keyword + "%"));
                predicatesForKeyword.add(cb.equal(root.join(PickMeUpTicket_.pickMeUp).get(PickMeUp_.category1st).get(Category_.name), keyword));
                predicatesForKeyword.add(cb.equal(root.join(PickMeUpTicket_.pickMeUp).join(PickMeUp_.user).join(User_.categories, JoinType.LEFT).get(Category_.name), keyword));
                predicates.add(cb.or(predicatesForKeyword.toArray(new Predicate[predicatesForKeyword.size()])));
            }
            if (userId != null) {
                predicates.add(cb.equal(root.join(PickMeUpTicket_.pickMeUp).get(PickMeUp_.user).get(User_.id), userId));
            }
            if (onlyDirectMarketAvailable) {
                predicates.add(cb.equal(root.join(PickMeUpTicket_.freelancerProductItemType).get(FreelancerProductItemType_.code), FreelancerProductItemType.Code.DIRECT_DEAL));
            } else {
                predicates.add(cb.equal(root.join(PickMeUpTicket_.freelancerProductItemType).get(FreelancerProductItemType_.code), FreelancerProductItemType.Code.PICK_ME_UP));
            }

            query.groupBy(root.get(PickMeUpTicket_.pickMeUp));

            Predicate[] predicatesArray = predicates.toArray(new Predicate[predicates.size()]);
            return cb.and(predicatesArray);
        });
    }

    public static Specification<PickMeUpTicket> filterForActiveTicket(Long pickMeUpId) {

        return ((root, query, cb) -> {
            query.distinct(true);
            query.orderBy(
                    cb.desc(cb.selectCase().when(cb.equal(root.join(PickMeUpTicket_.pickMeUp).get(PickMeUp_.priority), true), 2).otherwise(1)),
                    cb.desc(root.get(PickMeUpTicket_.pickMeUp).get(PickMeUp_.createdAt))
            );
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.join(PickMeUpTicket_.pickMeUp).get(PickMeUp_.invalid), false));
            predicates.add(cb.equal(root.join(PickMeUpTicket_.pickMeUp).get(PickMeUp_.temp), false));
            if (pickMeUpId != null) {
                predicates.add(cb.equal(root.join(PickMeUpTicket_.pickMeUp).get(PickMeUp_.id), pickMeUpId));
            }
            predicates.add(cb.lessThanOrEqualTo(root.get(PickMeUpTicket_.startAt), LocalDateTime.now()));
            predicates.add(cb.greaterThanOrEqualTo(root.get(PickMeUpTicket_.endAt), LocalDateTime.now()));

            Predicate[] predicatesArray = predicates.toArray(new Predicate[predicates.size()]);
            return cb.and(predicatesArray);
        });
    }

    public static Specification<PickMeUpTicket> filterForActiveTicketForUser(Long userId) {

        return ((root, query, cb) -> {
            query.distinct(true);
            query.orderBy(
                    cb.desc(cb.selectCase().when(cb.equal(root.join(PickMeUpTicket_.pickMeUp).get(PickMeUp_.priority), true), 2).otherwise(1)),
                    cb.desc(root.get(PickMeUpTicket_.pickMeUp).get(PickMeUp_.createdAt))
            );
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.join(PickMeUpTicket_.pickMeUp).get(PickMeUp_.invalid), false));
            predicates.add(cb.equal(root.join(PickMeUpTicket_.pickMeUp).get(PickMeUp_.temp), false));
            if (userId != null) {
                predicates.add(cb.equal(root.join(PickMeUpTicket_.pickMeUp).get(PickMeUp_.user).get(User_.id), userId));
            }
            predicates.add(cb.lessThanOrEqualTo(root.get(PickMeUpTicket_.startAt), LocalDateTime.now()));
            predicates.add(cb.greaterThanOrEqualTo(root.get(PickMeUpTicket_.endAt), LocalDateTime.now()));

            Predicate[] predicatesArray = predicates.toArray(new Predicate[predicates.size()]);
            return cb.and(predicatesArray);
        });
    }
}
