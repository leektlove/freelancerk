package com.freelancerk.domain.specification;

import com.freelancerk.domain.*;
import com.freelancerk.io.KpiOrdering;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class KpiFreelancerSpecifications {

    public static Specification<KpiFreelancer> filterSpecificSearch(KpiOrdering orderBy, Sort.Direction direction,
                                                                final String keyword,
                                                                final Long categoryId,
                                                                final LocalDateTime createdFrom,
                                                                final LocalDateTime createdTo,
                                                                final String userKeyword,
                                                                final Boolean useEscrow,
                                                                final Boolean leave,
                                                                final User.BusinessType businessType,
                                                                final Long projectCountFrom,
                                                                final Long projectCountTo,
                                                                final Long transactionAmountFrom,
                                                                final Long transactionAmountTo,
                                                                final Float ratingFrom,
                                                                final Float ratingTo) {
        return (root, query, cb) -> {
            query.distinct(true);
            if (orderBy != null) {
                if (KpiOrdering.ACCESS.equals(orderBy)) {
                    if (Sort.Direction.DESC.equals(direction)) {
                        query.orderBy(cb.desc(root.get(KpiFreelancer_.numberOfAccessCount)));
                    } else {
                        query.orderBy(cb.asc(root.get(KpiFreelancer_.numberOfAccessCount)));
                    }
                } else if (KpiOrdering.NO.equals(orderBy)) {
                    if (Sort.Direction.DESC.equals(direction)) {
                        query.orderBy(cb.desc(root.get(KpiFreelancer_.id)));
                    } else {
                        query.orderBy(cb.asc(root.get(KpiFreelancer_.id)));
                    }
                } else if (KpiOrdering.NAME.equals(orderBy)) {
                    if (Sort.Direction.DESC.equals(direction)) {
                        query.orderBy(cb.desc(root.get(KpiFreelancer_.user).get(User_.name)));
                    } else {
                        query.orderBy(cb.asc(root.get(KpiFreelancer_.user).get(User_.name)));
                    }
                } else if (KpiOrdering.NICKNAME.equals(orderBy)) {
                    if (Sort.Direction.DESC.equals(direction)) {
                        query.orderBy(cb.desc(root.get(KpiFreelancer_.user).get(User_.nickname)));
                    } else {
                        query.orderBy(cb.asc(root.get(KpiFreelancer_.user).get(User_.nickname)));
                    }
                } else if (KpiOrdering.PROJECT.equals(orderBy)) {
                    if (Sort.Direction.DESC.equals(direction)) {
                        query.orderBy(cb.desc(root.get(KpiFreelancer_.numberOfProjectBids)));
                    } else {
                        query.orderBy(cb.asc(root.get(KpiFreelancer_.numberOfProjectBids)));
                    }
                } else if (KpiOrdering.CONTEST.equals(orderBy)) {
                    if (Sort.Direction.DESC.equals(direction)) {
                        query.orderBy(cb.desc(root.get(KpiFreelancer_.numberOfContestEntries)));
                    } else {
                        query.orderBy(cb.asc(root.get(KpiFreelancer_.numberOfContestEntries)));
                    }
                } else if (KpiOrdering.PROPOSITION.equals(orderBy)) {
                    if (Sort.Direction.DESC.equals(direction)) {
                        query.orderBy(cb.desc(root.get(KpiFreelancer_.numberOfProjectPropositionCount)));
                    } else {
                        query.orderBy(cb.asc(root.get(KpiFreelancer_.numberOfProjectPropositionCount)));
                    }
                } else if (KpiOrdering.RATINGS.equals(orderBy)) {
                    if (Sort.Direction.DESC.equals(direction)) {
                        query.orderBy(cb.desc(root.get(KpiFreelancer_.ratings)));
                    } else {
                        query.orderBy(cb.asc(root.get(KpiFreelancer_.ratings)));
                    }
                } else if (KpiOrdering.TRANSACTION_AMOUNT.equals(orderBy)) {
                    if (Sort.Direction.DESC.equals(direction)) {
                        query.orderBy(cb.desc(root.get(KpiFreelancer_.totalTransactionAmount)));
                    } else {
                        query.orderBy(cb.asc(root.get(KpiFreelancer_.totalTransactionAmount)));
                    }
                } else if (KpiOrdering.COMPLETED.equals(orderBy)) {
                    if (Sort.Direction.DESC.equals(direction)) {
                        query.orderBy(cb.desc(root.get(KpiFreelancer_.numberOfCompletedProject)));
                    } else {
                        query.orderBy(cb.asc(root.get(KpiFreelancer_.numberOfCompletedProject)));
                    }
                } else if (KpiOrdering.TOTAL_OPTION_COUNT.equals(orderBy)) {
                    if (Sort.Direction.DESC.equals(direction)) {
                        query.orderBy(cb.desc(root.get(KpiFreelancer_.numberOfOptionCount)));
                    } else {
                        query.orderBy(cb.asc(root.get(KpiFreelancer_.numberOfOptionCount)));
                    }
                } else if (KpiOrdering.TOTAL_OPTION_AMOUNT.equals(orderBy)) {
                    if (Sort.Direction.DESC.equals(direction)) {
                        query.orderBy(cb.desc(root.get(KpiFreelancer_.totalChargedOptionPrice)));
                    } else {
                        query.orderBy(cb.asc(root.get(KpiFreelancer_.totalChargedOptionPrice)));
                    }
                } else if (KpiOrdering.POINT.equals(orderBy)) {
                    if (Sort.Direction.DESC.equals(direction)) {
                        query.orderBy(cb.desc(root.get(KpiFreelancer_.points)));
                    } else {
                        query.orderBy(cb.asc(root.get(KpiFreelancer_.points)));
                    }
                }
            } else {
                query.orderBy(cb.desc(root.get(KpiFreelancer_.id)));
            }

            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.isNotEmpty(keyword)) {
                String newKeyword = keyword;
                if (newKeyword.startsWith("#")) {
                    newKeyword = keyword.substring(1, newKeyword.length());
                }
                List<Predicate> predicatesForKeyword = new ArrayList<>();
                predicatesForKeyword.add(cb.like(root.get(KpiFreelancer_.user).get(User_.name), "%" + newKeyword + "%"));
                predicatesForKeyword.add(cb.like(root.get(KpiFreelancer_.user).get(User_.nickname), "%" + newKeyword + "%"));
                predicatesForKeyword.add(cb.like(root.get(KpiFreelancer_.user).get(User_.corporateName), "%" + newKeyword + "%"));
                predicatesForKeyword.add(cb.like(root.get(KpiFreelancer_.user).get(User_.cellphone), "%" + newKeyword + "%"));
                predicatesForKeyword.add(cb.like(root.get(KpiFreelancer_.user).get(User_.email), "%" + newKeyword + "%"));
                predicates.add(cb.or(predicatesForKeyword.toArray(new Predicate[predicatesForKeyword.size()])));
            }
            if (categoryId != null) {
                predicates.add(cb.equal(root.join(KpiFreelancer_.user).join(User_.categories, JoinType.LEFT).get(Category_.id), categoryId));
            }
            if (StringUtils.isNotEmpty(userKeyword)) {
                predicates.add(cb.like(root.join(KpiFreelancer_.user).join(User_.categories, JoinType.LEFT).get(Category_.name), "%" + userKeyword + "%"));
            }
            if (useEscrow != null) {
                predicates.add(cb.equal(root.get(KpiFreelancer_.user).get(User_.useEscrow), useEscrow));
            }
            if (leave != null) {
                predicates.add(cb.equal(root.get(KpiFreelancer_.user).get(User_.leaved), leave));
            }
            if (businessType != null) {
                predicates.add(cb.equal(root.get(KpiFreelancer_.user).get(User_.businessType), businessType));
            }
            if (createdFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get(KpiFreelancer_.user).get(User_.createdAt), createdFrom));
            }
            if (createdTo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get(KpiFreelancer_.user).get(User_.createdAt), createdTo));
            }
            if (projectCountFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get(KpiFreelancer_.numberOfCompletedProject), projectCountFrom));
            }
            if (projectCountTo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get(KpiFreelancer_.numberOfCompletedProject), projectCountTo));
            }
            if (transactionAmountFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get(KpiFreelancer_.totalTransactionAmount), transactionAmountFrom));
            }
            if (transactionAmountTo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get(KpiFreelancer_.totalTransactionAmount), transactionAmountTo));
            }
            if (ratingFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get(KpiFreelancer_.ratings), ratingFrom));
            }
            if (ratingTo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get(KpiFreelancer_.ratings), ratingTo));
            }

            Predicate[] predicatesArray = predicates.toArray(new Predicate[predicates.size()]);
            return cb.and(predicatesArray);
        };
    }
}
