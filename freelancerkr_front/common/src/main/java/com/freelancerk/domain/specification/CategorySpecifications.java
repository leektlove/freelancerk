package com.freelancerk.domain.specification;

import com.freelancerk.domain.Category;
import com.freelancerk.domain.Category_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class CategorySpecifications {

    public static Specification<Category> filterForTopCategory(String keyword) {
        return (root, query, cb) -> {
            query.distinct(true);

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.isNull(root.get(Category_.parentCategory).get(Category_.parentCategory).get(Category_.parentCategory)));
            predicates.add(cb.like(root.get(Category_.name), "%" + keyword + "%"));

            Predicate[] predicatesArray = predicates.toArray(new Predicate[predicates.size()]);
            return cb.and(predicatesArray);
        };
    }
}
