package com.freelancerk.domain.specification;

import com.freelancerk.domain.ProjectComment;
import com.freelancerk.domain.ProjectComment_;
import com.freelancerk.domain.Project_;
import com.freelancerk.domain.User_;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class ProjectCommentSpecifications {

    public static Specification<ProjectComment> filterForFreelancer(final Long projectId, final Long clientUserId, final Long targetUserId) {
        return (root, query, cb) -> {
            query.distinct(true);

            List<Predicate> predicates = new ArrayList<>();
            List<Predicate> predicatesForAll = new ArrayList<>();
            predicatesForAll.add(cb.equal(root.get(ProjectComment_.user).get(User_.id), clientUserId));
            predicatesForAll.add(root.get(ProjectComment_.targetUser).isNull());

            List<Predicate> predicatesForFreelancer = new ArrayList<>();
            predicatesForFreelancer.add(cb.equal(root.get(ProjectComment_.user).get(User_.id), clientUserId));
            predicatesForFreelancer.add(cb.equal(root.get(ProjectComment_.targetUser).get(User_.id), targetUserId));

            List<Predicate> predicatesForFreelancerWrite = new ArrayList<>();
            predicatesForFreelancerWrite.add(cb.equal(root.get(ProjectComment_.user).get(User_.id), targetUserId));

            predicates.add(cb.or(cb.and(predicatesForAll.toArray(new Predicate[predicatesForAll.size()])),
                    cb.and(predicatesForFreelancer.toArray(new Predicate[predicatesForFreelancer.size()])),
                    cb.and(predicatesForFreelancerWrite.toArray(new Predicate[predicatesForFreelancerWrite.size()]))));

            if (projectId != null) {
                predicates.add(cb.equal(root.get(ProjectComment_.project).get(Project_.id), projectId));
            }

            Predicate[] predicatesArray = predicates.toArray(new Predicate[predicates.size()]);
            return cb.and(predicatesArray);
        };
    }

    public static Specification<ProjectComment> filterForInProgressForClient(Long projectId, Long clientUserId, Long freelancerUserId) {
        return (root, query, cb) -> {
            query.distinct(true);

            List<Predicate> predicates = new ArrayList<>();

            List<Predicate> predicatesForFreelancer = new ArrayList<>();
            predicatesForFreelancer.add(cb.equal(root.get(ProjectComment_.user).get(User_.id), freelancerUserId));

            List<Predicate> predicatesForClient = new ArrayList<>();
            predicatesForClient.add(cb.equal(root.get(ProjectComment_.user).get(User_.id), clientUserId));

            predicates.add(cb.or(
                    cb.and(predicatesForFreelancer.toArray(new Predicate[predicatesForFreelancer.size()])),
                    cb.and(predicatesForClient.toArray(new Predicate[predicatesForClient.size()]))));

            predicates.add(cb.equal(root.get(ProjectComment_.project).get(Project_.id), projectId));

            Predicate[] predicatesArray = predicates.toArray(new Predicate[predicates.size()]);
            return cb.and(predicatesArray);
        };
    }
}
