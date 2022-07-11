package com.freelancerk.domain.specification;

import com.freelancerk.domain.*;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DailyAccessLogSpecifications {

    public static Specification<DailyAccessLog> filter(LocalDate accessAtFrom, LocalDate accessAtTo) {
        return ((root, query, cb) -> {
            query.distinct(true);
            query.orderBy(cb.desc(root.get(DailyAccessLog_.createdAt)));
            List<Predicate> predicates = new ArrayList<>();

            if (accessAtFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get(DailyAccessLog_.date), accessAtFrom));
            }

            if (accessAtTo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get(DailyAccessLog_.date), accessAtTo));
            }

            Predicate[] predicatesArray = predicates.toArray(new Predicate[predicates.size()]);
            return cb.and(predicatesArray);
        });

    }
}
