package com.freelancerk.domain.specification;

import com.freelancerk.domain.*;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class DirectDealSpecifications {

    public static Specification<DirectDeal> filter(Long userId, FreelancerProductItemType includeItemType) {
        return ((root, query, cb) -> {
            query.distinct(true);
            query.orderBy(cb.desc(root.get(DirectDeal_.createdAt)));
            List<Predicate> predicates = new ArrayList<>();
            if (userId != null) {
                predicates.add(cb.equal(root.join(DirectDeal_.user).get(User_.id), userId));
            }

            if (includeItemType != null) {
                predicates.add(cb.isTrue(root.get(DirectDeal_.pickMeUp).get(PickMeUp_.directDeal)));
            }

            Predicate[] predicatesArray = predicates.toArray(new Predicate[predicates.size()]);
            return cb.and(predicatesArray);
        });

    }
}
