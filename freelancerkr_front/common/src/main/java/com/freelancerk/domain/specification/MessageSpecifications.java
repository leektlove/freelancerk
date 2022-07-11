package com.freelancerk.domain.specification;

import com.freelancerk.domain.Message;
import com.freelancerk.domain.Message_;
import com.freelancerk.domain.User_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.JoinType;

public class MessageSpecifications {

    public static Specification<Message> textInAllColumns(final String keyword, final Long userId) {
        return (root, query, cb) -> {
            query.distinct(true);
            return cb.or(
                    cb.like(root.get(Message_.title), "%" + keyword + "%"),
                    cb.like(root.get(Message_.content), "%" + keyword + "%"),
                    cb.equal(root.join(Message_.user, JoinType.LEFT).get(User_.id), userId)
            );
        };
    }
}
