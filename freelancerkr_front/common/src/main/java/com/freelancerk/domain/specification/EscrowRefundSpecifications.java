package com.freelancerk.domain.specification;

import com.freelancerk.domain.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EscrowRefundSpecifications {

    public static Specification<EscrowRefundRequest> searchProjectClientTransactions(List<EscrowRefundRequest.Type> types, Long clientUserId) {

        return (root, query, cb) -> {
            query.distinct(true);
            List<Predicate> predicates = new ArrayList<>();
            if (types != null && types.size() > 0) {
                predicates.add(root.get(EscrowRefundRequest_.type).in(types));
            }
            if (clientUserId != null) {
                predicates.add(cb.equal(root.join(EscrowRefundRequest_.user).get(User_.id), clientUserId));
            }
            Predicate[] predicatesArray = predicates.toArray(new Predicate[predicates.size()]);
            return cb.and(predicatesArray);
        };
    }

    public static Specification<EscrowRefundRequest> filterForAdmin(String keyword, EscrowRefundRequest.Type[] types,
                                                                    LocalDateTime createdAtFrom, LocalDateTime createdAtTo,
                                                                    LocalDateTime refundedAtFrom, LocalDateTime refundedAtTo) {

        return (root, query, cb) -> {
            query.distinct(true);
            List<Predicate> predicates = new ArrayList<>();
            if (types != null && types.length > 0) {
                predicates.add(root.get(EscrowRefundRequest_.type).in(types));
            }
            if (StringUtils.isNotEmpty(keyword)) {
                String newKeyword = keyword;
                List<Predicate> predicatesForKeyword = new ArrayList<>();
                predicatesForKeyword.add(cb.like(root.get(EscrowRefundRequest_.user).get(User_.corporateName), "%" + newKeyword + "%"));
                predicatesForKeyword.add(cb.like(root.get(EscrowRefundRequest_.user).get(User_.nickname), "%" + newKeyword + "%"));
                predicatesForKeyword.add(cb.like(root.get(EscrowRefundRequest_.user).get(User_.bankAccountName), "%" + newKeyword + "%"));
                predicatesForKeyword.add(cb.like(root.get(EscrowRefundRequest_.user).get(User_.bankAccountForReceivingPayment), "%" + newKeyword + "%"));
                predicatesForKeyword.add(cb.like(root.get(EscrowRefundRequest_.user).get(User_.bankForReceivingPayment).get(BankType_.name), "%" + newKeyword + "%"));
                predicates.add(cb.or(predicatesForKeyword.toArray(new Predicate[predicatesForKeyword.size()])));
            }

            if (createdAtFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get(EscrowRefundRequest_.createdAt), createdAtFrom));
            }
            if (createdAtTo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get(EscrowRefundRequest_.createdAt), createdAtTo));
            }

            if (refundedAtFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get(EscrowRefundRequest_.refundedAt), refundedAtFrom));
            }
            if (refundedAtTo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get(EscrowRefundRequest_.refundedAt), refundedAtTo));
            }

            Predicate[] predicatesArray = predicates.toArray(new Predicate[predicates.size()]);
            return cb.and(predicatesArray);
        };
    }
}
