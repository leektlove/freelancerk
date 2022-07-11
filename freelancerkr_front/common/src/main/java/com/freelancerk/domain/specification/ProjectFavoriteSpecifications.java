package com.freelancerk.domain.specification;

import com.freelancerk.domain.*;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class ProjectFavoriteSpecifications {

    public static Specification<ProjectFavorite> filter(Long userId) {
        return (root, query, cb) -> {
            query.distinct(true);
            query.orderBy(
                    cb.desc(root.get(ProjectFavorite_.createdAt))
            );
            List<Predicate> predicates = new ArrayList<>();
            if (userId != null) {
                predicates.add(cb.equal(root.get(ProjectFavorite_.user).get(User_.id), userId));
            }
            predicates.add(cb.equal(root.get(ProjectFavorite_.project).get(Project_.status), Project.Status.POSTED));

            Predicate[] predicatesArray = predicates.toArray(new Predicate[predicates.size()]);
            return cb.and(predicatesArray);
        };
    }
}
