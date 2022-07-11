package com.freelancerk.domain.specification;

import com.freelancerk.domain.Audition;
import com.freelancerk.domain.Category;
import com.freelancerk.domain.Category_;
import com.freelancerk.domain.EscrowRefundRequest_;
import com.freelancerk.domain.User;
import com.freelancerk.domain.User_;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AuditionSpecifications {
	
	public static Specification<User> filterForAudition2() {
		
        return (root, query, cb) -> {
            query.distinct(true);

            List<Predicate> predicates = new ArrayList<>();

            Predicate[] predicatesArray = predicates.toArray(new Predicate[predicates.size()]);
            return cb.and(predicatesArray);
        };
    }
	
    public static Specification<Audition> filterForAudition() {
        return (root, query, cb) -> {
            query.distinct(true);

            List<Predicate> predicates = new ArrayList<>();

            Predicate[] predicatesArray = predicates.toArray(new Predicate[predicates.size()]);
            return cb.and(predicatesArray);
        };
    }
    
}
