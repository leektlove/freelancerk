package com.freelancerk.domain.specification;

import com.freelancerk.domain.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PaymentToUserSpecifications {

    public static Specification<PaymentToUser> filterForAdmin(final String keyword,
                                                        final LocalDateTime depositAtFrom,
                                                        final LocalDateTime depositAtTo) {
        return (root, query, cb) -> {
            query.distinct(true);
            query.orderBy(cb.desc(root.get(PaymentToUser_.id)));

            List<Predicate> predicates = new ArrayList<>();
            predicates.add(root.get(PaymentToUser_.status).in(Arrays.asList(PaymentToUser.Status.ACCEPTED, PaymentToUser.Status.REQUESTED, PaymentToUser.Status.PAYED)));
            if (StringUtils.isNotEmpty(keyword)) {
                String newKeyword = keyword;
                List<Predicate> predicatesForKeyword = new ArrayList<>();
                predicatesForKeyword.add(cb.like(root.get(PaymentToUser_.project).get(Project_.postingClient).get(User_.corporateName), "%" + newKeyword + "%"));
                predicatesForKeyword.add(cb.like(root.get(PaymentToUser_.project).get(Project_.postingClient).get(User_.nickname), "%" + newKeyword + "%"));
                predicatesForKeyword.add(cb.like(root.get(PaymentToUser_.project).get(Project_.postingClient).get(User_.bankAccountName), "%" + newKeyword + "%"));
                predicatesForKeyword.add(cb.like(root.get(PaymentToUser_.project).get(Project_.postingClient).get(User_.bankAccountForReceivingPayment), "%" + newKeyword + "%"));
                predicatesForKeyword.add(cb.like(root.get(PaymentToUser_.project).get(Project_.postingClient).get(User_.bankForReceivingPayment).get(BankType_.name), "%" + newKeyword + "%"));
                predicates.add(cb.or(predicatesForKeyword.toArray(new Predicate[predicatesForKeyword.size()])));
            }

            if (depositAtFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get(PaymentToUser_.depositAt), depositAtFrom));
            }
            if (depositAtTo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get(PaymentToUser_.depositAt), depositAtTo));
            }

            Predicate[] predicatesArray = predicates.toArray(new Predicate[predicates.size()]);
            return cb.and(predicatesArray);
        };
    }

    public static Specification<PaymentToUser> filterForPaymentAdmin(String keyword, PaymentToUser.Status status, LocalDateTime createdAtFrom, LocalDateTime createdAtTo) {
        return (root, query, cb) -> {
            query.distinct(true);
            query.orderBy(cb.desc(root.get(PaymentToUser_.id)));

            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.isNotEmpty(keyword)) {
                String newKeyword = keyword;
                List<Predicate> predicatesForKeyword = new ArrayList<>();
                predicatesForKeyword.add(cb.like(root.get(PaymentToUser_.project).get(Project_.postingClient).get(User_.corporateName), "%" + newKeyword + "%"));
                predicatesForKeyword.add(cb.like(root.get(PaymentToUser_.project).get(Project_.postingClient).get(User_.nickname), "%" + newKeyword + "%"));
                predicatesForKeyword.add(cb.like(root.get(PaymentToUser_.project).get(Project_.postingClient).get(User_.bankAccountName), "%" + newKeyword + "%"));
                predicatesForKeyword.add(cb.like(root.get(PaymentToUser_.project).get(Project_.postingClient).get(User_.bankAccountForReceivingPayment), "%" + newKeyword + "%"));
                predicatesForKeyword.add(cb.like(root.get(PaymentToUser_.project).get(Project_.postingClient).get(User_.bankForReceivingPayment).get(BankType_.name), "%" + newKeyword + "%"));
                predicates.add(cb.or(predicatesForKeyword.toArray(new Predicate[predicatesForKeyword.size()])));
            }

            if (createdAtFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get(PaymentToUser_.createdAt), createdAtFrom));
            }
            if (createdAtTo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get(PaymentToUser_.createdAt), createdAtTo));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get(PaymentToUser_.status), status));
            }

            Predicate[] predicatesArray = predicates.toArray(new Predicate[predicates.size()]);
            return cb.and(predicatesArray);
        };
    }
}
