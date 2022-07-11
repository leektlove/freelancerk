package com.freelancerk.domain.specification;

import com.freelancerk.domain.*;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class FreelancerPayProductSpecifications {

    public static Specification<FreelancerPayProduct> filter(List<FreelancerProductItemType> containItems, List<Long> excludePurchaseId,
                                                             FreelancerPayProduct.Type type) {
        return ((root, query, cb) -> {
            query.distinct(true);
            List<Predicate> predicates = new ArrayList<>();
            if (type != null) {
                predicates.add(cb.equal(root.get(FreelancerPayProduct_.type), type));
            }
            if (containItems != null && containItems.size() > 0) {
                predicates.add(root.get(FreelancerPayProduct_.freelancerProductItemType).in(containItems));
            }

            if (excludePurchaseId != null && excludePurchaseId.size() > 0) {
                predicates.add(root.get(FreelancerPayProduct_.purchase).get(Purchase_.id).in(excludePurchaseId).not());
            }

            query.groupBy(root.get(FreelancerPayProduct_.pickMeUp));

            Predicate[] predicatesArray = predicates.toArray(new Predicate[predicates.size()]);
            return cb.and(predicatesArray);
        });

    }
}
