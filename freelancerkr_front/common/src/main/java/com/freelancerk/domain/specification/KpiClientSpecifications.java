package com.freelancerk.domain.specification;

import com.freelancerk.domain.*;
import com.freelancerk.io.KpiOrdering;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class KpiClientSpecifications {

    public static Specification<KpiClient> filterSpecificSearch(KpiOrdering orderBy, Sort.Direction direction,
                                                                final String keyword,
                                                                final Boolean useEscrow,
                                                                final Boolean leave,
                                                                final LocalDateTime createdFrom,
                                                                final LocalDateTime createdTo,
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
                        query.orderBy(cb.desc(root.get(KpiClient_.numberOfAccessCount)));
                    } else {
                        query.orderBy(cb.asc(root.get(KpiClient_.numberOfAccessCount)));
                    }
                } else if (KpiOrdering.NO.equals(orderBy)) {
                    if (Sort.Direction.DESC.equals(direction)) {
                        query.orderBy(cb.desc(root.get(KpiClient_.id)));
                    } else {
                        query.orderBy(cb.asc(root.get(KpiClient_.id)));
                    }
                } else if (KpiOrdering.NAME.equals(orderBy)) {
                    if (Sort.Direction.DESC.equals(direction)) {
                        query.orderBy(cb.desc(root.get(KpiClient_.user).get(User_.name)));
                    } else {
                        query.orderBy(cb.asc(root.get(KpiClient_.user).get(User_.name)));
                    }
                } else if (KpiOrdering.NICKNAME.equals(orderBy)) {
                    if (Sort.Direction.DESC.equals(direction)) {
                        query.orderBy(cb.desc(root.get(KpiClient_.user).get(User_.nickname)));
                    } else {
                        query.orderBy(cb.asc(root.get(KpiClient_.user).get(User_.nickname)));
                    }
                } else if (KpiOrdering.PROJECT.equals(orderBy)) {
                    if (Sort.Direction.DESC.equals(direction)) {
                        query.orderBy(cb.desc(root.get(KpiClient_.numberOfProjectCount)));
                    } else {
                        query.orderBy(cb.asc(root.get(KpiClient_.numberOfProjectCount)));
                    }
                } else if (KpiOrdering.CONTEST.equals(orderBy)) {
                    if (Sort.Direction.DESC.equals(direction)) {
                        query.orderBy(cb.desc(root.get(KpiClient_.numberOfContestCount)));
                    } else {
                        query.orderBy(cb.asc(root.get(KpiClient_.numberOfContestCount)));
                    }
                } else if (KpiOrdering.PROPOSITION.equals(orderBy)) {
                    if (Sort.Direction.DESC.equals(direction)) {
                        query.orderBy(cb.desc(root.get(KpiClient_.numberOfProjectPropositionCount)));
                    } else {
                        query.orderBy(cb.asc(root.get(KpiClient_.numberOfProjectPropositionCount)));
                    }
                } else if (KpiOrdering.DIRECT.equals(orderBy)) {
                    if (Sort.Direction.DESC.equals(direction)) {
                        query.orderBy(cb.desc(root.get(KpiClient_.numberOfDirectDealCount)));
                    } else {
                        query.orderBy(cb.asc(root.get(KpiClient_.numberOfDirectDealCount)));
                    }
                } else if (KpiOrdering.RATINGS.equals(orderBy)) {
                    if (Sort.Direction.DESC.equals(direction)) {
                        query.orderBy(cb.desc(root.get(KpiClient_.ratings)));
                    } else {
                        query.orderBy(cb.asc(root.get(KpiClient_.ratings)));
                    }
                } else if (KpiOrdering.TRANSACTION_AMOUNT.equals(orderBy)) {
                    if (Sort.Direction.DESC.equals(direction)) {
                        query.orderBy(cb.desc(root.get(KpiClient_.totalTransactionAmount)));
                    } else {
                        query.orderBy(cb.asc(root.get(KpiClient_.totalTransactionAmount)));
                    }
                } else if (KpiOrdering.COMPLETED.equals(orderBy)) {
                    if (Sort.Direction.DESC.equals(direction)) {
                        query.orderBy(cb.desc(root.get(KpiClient_.numberOfCompletedProject)));
                    } else {
                        query.orderBy(cb.asc(root.get(KpiClient_.numberOfCompletedProject)));
                    }
                } else if (KpiOrdering.TOTAL_OPTION_COUNT.equals(orderBy)) {
                    if (Sort.Direction.DESC.equals(direction)) {
                        query.orderBy(cb.desc(root.get(KpiClient_.numberOfOptionCount)));
                    } else {
                        query.orderBy(cb.asc(root.get(KpiClient_.numberOfOptionCount)));
                    }
                } else if (KpiOrdering.TOTAL_OPTION_AMOUNT.equals(orderBy)) {
                    if (Sort.Direction.DESC.equals(direction)) {
                        query.orderBy(cb.desc(root.get(KpiClient_.totalChargedOptionPrice)));
                    } else {
                        query.orderBy(cb.asc(root.get(KpiClient_.totalChargedOptionPrice)));
                    }
                } else if (KpiOrdering.POINT.equals(orderBy)) {
                    if (Sort.Direction.DESC.equals(direction)) {
                        query.orderBy(cb.desc(root.get(KpiClient_.points)));
                    } else {
                        query.orderBy(cb.asc(root.get(KpiClient_.points)));
                    }
                }
            } else {
                query.orderBy(cb.desc(root.get(KpiClient_.id)));
            }

            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.isNotEmpty(keyword)) {
                String newKeyword = keyword;
                if (newKeyword.startsWith("#")) {
                    newKeyword = keyword.substring(1, newKeyword.length());
                }
                List<Predicate> predicatesForKeyword = new ArrayList<>();
                predicatesForKeyword.add(cb.like(root.get(KpiClient_.user).get(User_.name), "%" + newKeyword + "%"));
                predicatesForKeyword.add(cb.like(root.get(KpiClient_.user).get(User_.nickname), "%" + newKeyword + "%"));
                predicatesForKeyword.add(cb.like(root.get(KpiClient_.user).get(User_.corporateName), "%" + newKeyword + "%"));
                predicatesForKeyword.add(cb.like(root.get(KpiClient_.user).get(User_.cellphone), "%" + newKeyword + "%"));
                predicatesForKeyword.add(cb.like(root.get(KpiClient_.user).get(User_.email), "%" + newKeyword + "%"));
                predicates.add(cb.or(predicatesForKeyword.toArray(new Predicate[predicatesForKeyword.size()])));
            }
            if (useEscrow != null) {
                predicates.add(cb.equal(root.get(KpiClient_.user).get(User_.useEscrow), useEscrow));
            }
            if (leave != null) {
                predicates.add(cb.equal(root.get(KpiClient_.user).get(User_.leaved), leave));
            }
            if (businessType != null) {
                predicates.add(cb.equal(root.get(KpiClient_.user).get(User_.businessType), businessType));
            }
            if (createdFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get(KpiClient_.user).get(User_.createdAt), createdFrom));
            }
            if (createdTo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get(KpiClient_.user).get(User_.createdAt), createdTo));
            }
            if (projectCountFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get(KpiClient_.numberOfCompletedProject), projectCountFrom));
            }
            if (projectCountTo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get(KpiClient_.numberOfCompletedProject), projectCountTo));
            }
            if (transactionAmountFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get(KpiClient_.totalTransactionAmount), transactionAmountFrom));
            }
            if (transactionAmountTo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get(KpiClient_.totalTransactionAmount), transactionAmountTo));
            }
            if (ratingFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get(KpiClient_.ratings), ratingFrom));
            }
            if (ratingTo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get(KpiClient_.ratings), ratingTo));
            }

            Predicate[] predicatesArray = predicates.toArray(new Predicate[predicates.size()]);
            return cb.and(predicatesArray);
        };
    }
}
