package com.freelancerk.domain.specification;

import com.freelancerk.domain.EscrowLog;
import com.freelancerk.domain.EscrowLog_;
import com.freelancerk.domain.User_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EscrowLogSpecifications {

    public static Specification<EscrowLog> searchProjectClientTransactions(
            List<EscrowLog.Type> types, LocalDateTime startAt, LocalDateTime endAt, Long clientUserId) {

        return (root, query, cb) -> {
            query.distinct(true);
            List<Predicate> predicates = new ArrayList<>();
            if (types != null && types.size() > 0) {
                predicates.add(root.get(EscrowLog_.type).in(types));
            }
            if (startAt != null) {
                predicates.add(cb.greaterThan(root.get(EscrowLog_.createdAt), startAt));
            }
            if (endAt != null) {
                predicates.add(cb.lessThan(root.get(EscrowLog_.createdAt), endAt));
            }
            if (clientUserId != null) {
                List<Predicate> predicateList = new ArrayList<>();
                predicateList.add(cb.equal(root.get(EscrowLog_.depositUser).get(User_.id), clientUserId));
                predicateList.add(cb.equal(root.get(EscrowLog_.withdrawalUser).get(User_.id), clientUserId));
                predicates.add(cb.or(predicateList.toArray(new Predicate[predicateList.size()])));
            }
            Predicate[] predicatesArray = predicates.toArray(new Predicate[predicates.size()]);
            return cb.and(predicatesArray);
        };
    }

    public static Specification<EscrowLog> searchProjectFreelancerTransactions(
            List<EscrowLog.Type> types, LocalDateTime startAt, LocalDateTime endAt, Long freelancerUserId) {

        return (root, query, cb) -> {
            query.distinct(true);
            List<Predicate> predicates = new ArrayList<>();
            if (types != null && types.size() > 0) {
                predicates.add(root.get(EscrowLog_.type).in(types));
            }
            if (startAt != null) {
                predicates.add(cb.greaterThan(root.get(EscrowLog_.createdAt), startAt));
            }
            if (endAt != null) {
                predicates.add(cb.lessThan(root.get(EscrowLog_.createdAt), endAt));
            }
            if (freelancerUserId != null) {
                predicates.add(cb.equal(root.join(EscrowLog_.withdrawalUser).get(User_.id), freelancerUserId));
            }
            Predicate[] predicatesArray = predicates.toArray(new Predicate[predicates.size()]);
            return cb.and(predicatesArray);
        };
    }

    public static Specification<EscrowLog> filter(EscrowLog.Type type) {
        return (root, query, cb) -> {
            query.distinct(true);
            List<Predicate> predicates = new ArrayList<>();
            if (type != null) {
                predicates.add(cb.equal(root.get(EscrowLog_.type), type));
            }
            Predicate[] predicatesArray = predicates.toArray(new Predicate[predicates.size()]);
            return cb.and(predicatesArray);
        };
    }
}
