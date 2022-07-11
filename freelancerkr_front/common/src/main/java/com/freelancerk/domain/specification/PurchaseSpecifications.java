package com.freelancerk.domain.specification;

import com.freelancerk.domain.Purchase;
import com.freelancerk.domain.Purchase_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class PurchaseSpecifications {

    public static Specification<Purchase> filter(Purchase.Status status) {

        return ((root, query, cb) -> {
            query.distinct(true);
            List<Predicate> predicates = new ArrayList<>();
            if (status != null) {
                predicates.add(cb.equal(root.get(Purchase_.status), status));
            }

            Predicate[] predicatesArray = predicates.toArray(new Predicate[predicates.size()]);
            return cb.and(predicatesArray);
        });
    }
}
