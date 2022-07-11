package com.freelancerk.domain.specification;

import com.freelancerk.domain.*;
import com.freelancerk.model.SortBy;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProjectPropositionSpecifications {

    public static Specification<ProjectProposition> filterForClient(Long clientUserId, String keyword, LocalDateTime startAt, LocalDateTime endAt) {
        return ((root, query, cb) -> {
            query.distinct(true);
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.join(ProjectProposition_.project, JoinType.LEFT).get(Project_.postingClient).get(User_.id), clientUserId));
            predicates.add(cb.equal(root.join(ProjectProposition_.project, JoinType.LEFT).get(Project_.status), Project.Status.POSTED));
            if (startAt != null) {
                predicates.add(cb.greaterThan(root.get(ProjectProposition_.createdAt), startAt));
            }
            if (endAt != null) {
                predicates.add(cb.lessThan(root.get(ProjectProposition_.createdAt), endAt));
            }
            Predicate[] predicatesArray = predicates.toArray(new Predicate[predicates.size()]);
            return cb.and(predicatesArray);
        });
    }

    public static Specification<ProjectProposition> filterForFreelancer(Long freelancerUserId, SortBy sortBy) {
        return ((root, query, cb) -> {
            query.distinct(true);
            if (SortBy.CREATED_AT.equals(sortBy)) {
                query.orderBy(cb.desc(root.get(ProjectProposition_.createdAt)));
            } else if (SortBy.END_AT.equals(sortBy)) {
                query.orderBy(cb.asc(root.get(ProjectProposition_.project).get(Project_.postingEndAt)));
            }
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.join(ProjectProposition_.project, JoinType.LEFT).get(Project_.status), Project.Status.POSTED));
            predicates.add(cb.greaterThanOrEqualTo(root.join(ProjectProposition_.project, JoinType.LEFT).get(Project_.postingEndAt), LocalDateTime.now()));
            predicates.add(cb.equal(root.join(ProjectProposition_.freelancer, JoinType.LEFT).get(User_.id), freelancerUserId));
            predicates.add(cb.notEqual(root.get(ProjectProposition_.status), ProjectProposition.Status.REJECT));

            Predicate[] predicatesArray = predicates.toArray(new Predicate[predicates.size()]);
            return cb.and(predicatesArray);
        });
    }
}
