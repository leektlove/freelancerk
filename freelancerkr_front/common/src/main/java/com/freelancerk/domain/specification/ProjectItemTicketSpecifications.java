package com.freelancerk.domain.specification;

import com.freelancerk.domain.*;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProjectItemTicketSpecifications {

    public static Specification<ProjectItemTicket> filter(List<ProjectProductItemType> containItems,
                                                          List<Project.Status> excludeProjectStatuses,
                                                          Boolean filterPostingEnd, Long sessionUserId,
                                                          boolean fromPaymentView) {
        return ((root, query, cb) -> {
            query.distinct(true);
            if (fromPaymentView) {
                query.orderBy(cb.desc(root.get(ProjectItemTicket_.project).get(Project_.createdAt)));
            } else {
                query.orderBy(
                        cb.desc(cb.selectCase().when(cb.equal(root.join(ProjectItemTicket_.project).get(Project_.externalPriority), true), 2).otherwise(1)),
                        cb.desc(root.join(ProjectItemTicket_.project).get(Project_.createdAt))
                );
            }
            List<Predicate> predicates = new ArrayList<>();
            if (sessionUserId != null) {
                predicates.add(cb.equal(root.join(ProjectItemTicket_.project).join(Project_.postingClient).get(User_.id), sessionUserId));
            }

            if (excludeProjectStatuses != null) {
                predicates.add(root.get(ProjectItemTicket_.project).get(Project_.status).in(excludeProjectStatuses).not());
            }

            if (containItems != null && containItems.size() > 0) {
                predicates.add(cb.greaterThan(root.get(ProjectItemTicket_.expiredAt), LocalDateTime.now()));
                predicates.add(root.get(ProjectItemTicket_.projectProductItemType).in(containItems));
            }

            if (filterPostingEnd != null && filterPostingEnd) {
                predicates.add(cb.greaterThan(root.get(ProjectItemTicket_.project).get(Project_.postingEndAt), LocalDateTime.now()));
            }

            predicates.add(cb.equal(root.get(ProjectItemTicket_.project).get(Project_.invalid), false));
            if (fromPaymentView) {
                predicates.add(cb.equal(root.get(ProjectItemTicket_.project).get(Project_.purchaseRecordDeleted), false));
            }

            query.groupBy(root.get(ProjectItemTicket_.project));

            Predicate[] predicatesArray = predicates.toArray(new Predicate[predicates.size()]);
            return cb.and(predicatesArray);
        });

    }
}
