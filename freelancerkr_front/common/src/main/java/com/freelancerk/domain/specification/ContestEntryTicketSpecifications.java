package com.freelancerk.domain.specification;

import com.freelancerk.domain.*;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ContestEntryTicketSpecifications {

    public static Specification<ContestEntryTicket> filterForActiveTicketByContestEntryId(Long contestEntryId) {

        return ((root, query, cb) -> {
            query.distinct(true);
            query.orderBy(
//                    cb.desc(cb.selectCase().when(cb.equal(root.join(PickMeUpTicket_.pickMeUp).get(PickMeUp_.priority), true), 2).otherwise(1)),
                    cb.asc(root.get(ContestEntryTicket_.projectBid).get(ProjectBid_.applyAt))
            );
            List<Predicate> predicates = new ArrayList<>();
            if (contestEntryId != null) {
                predicates.add(cb.equal(root.join(ContestEntryTicket_.projectBid).get(ProjectBid_.id), contestEntryId));
            }
            predicates.add(cb.lessThanOrEqualTo(root.get(ContestEntryTicket_.createdAt), LocalDateTime.now()));
            predicates.add(cb.greaterThanOrEqualTo(root.get(ContestEntryTicket_.endAt), LocalDateTime.now()));

            Predicate[] predicatesArray = predicates.toArray(new Predicate[predicates.size()]);
            return cb.and(predicatesArray);
        });
    }

    public static Specification<ContestEntryTicket> filterForActiveTicketByUserIdAndProjectId(Long userId, Long projectId) {

        return ((root, query, cb) -> {
            query.distinct(true);
            query.orderBy(
                    cb.asc(root.get(ContestEntryTicket_.projectBid).get(ProjectBid_.applyAt))
            );
            List<Predicate> predicates = new ArrayList<>();
            if (userId != null) {
                predicates.add(cb.equal(root.join(ContestEntryTicket_.projectBid).get(ProjectBid_.participant).get(User_.id), userId));
            }
            if (projectId != null) {
                predicates.add(cb.equal(root.join(ContestEntryTicket_.projectBid).get(ProjectBid_.project).get(Project_.id), projectId));
            }
            predicates.add(cb.lessThanOrEqualTo(root.get(ContestEntryTicket_.createdAt), LocalDateTime.now()));
            predicates.add(cb.greaterThanOrEqualTo(root.get(ContestEntryTicket_.endAt), LocalDateTime.now()));

            Predicate[] predicatesArray = predicates.toArray(new Predicate[predicates.size()]);
            return cb.and(predicatesArray);
        });
    }
}
