package com.freelancerk.domain.specification;

import com.freelancerk.domain.*;
import com.freelancerk.model.SortBy;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProjectBidSpecifications {

    public static Specification<ProjectBid> filter(Long freelancerUserId, Long projectId, ProjectBid.BidType bidType,
                                                   ProjectBid.BidStatus status, Project.Status projectStatus, Project.Type projectType, SortBy sortBy) {
        return ((root, query, cb) -> {
            query.distinct(true);
            if (SortBy.CREATED_AT.equals(sortBy)) {
                query.orderBy(cb.desc(root.get(ProjectBid_.applyAt)));
            } else if (SortBy.END_AT.equals(sortBy)) {
                query.orderBy(cb.asc(root.get(ProjectBid_.project).get(Project_.postingEndAt)));
            } else if (SortBy.AMOUNT.equals(sortBy)) {
                query.orderBy(cb.desc(root.get(ProjectBid_.amountOfMoney)));
            }
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get(ProjectBid_.invalid), false));
            if (freelancerUserId != null) {
                predicates.add(cb.equal(root.get(ProjectBid_.participant), freelancerUserId));
            }
            if (projectId != null) {
                predicates.add(cb.equal(root.get(ProjectBid_.project), projectId));
            }
            if (bidType != null) {
                predicates.add(cb.equal(root.get(ProjectBid_.bidType), bidType));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get(ProjectBid_.bidStatus), status));
            }
            if (projectStatus != null) {
                predicates.add(cb.equal(root.join(ProjectBid_.project).get(Project_.status), projectStatus));
            }
            if (projectType != null) {
                predicates.add(cb.equal(root.join(ProjectBid_.project).get(Project_.projectType), projectType));
            }

            Predicate[] predicatesArray = predicates.toArray(new Predicate[predicates.size()]);
            return cb.and(predicatesArray);
        });

    }

    public static Specification<ProjectBid> filterForCancelOrFail(Long freelancerUserId, Project.Type projectType, SortBy sortBy) {
        return ((root, query, cb) -> {
            query.distinct(true);
            if (SortBy.CREATED_AT.equals(sortBy)) {
                query.orderBy(cb.desc(root.get(ProjectBid_.applyAt)));
            } else if (SortBy.END_AT.equals(sortBy)) {
                query.orderBy(cb.asc(root.get(ProjectBid_.project).get(Project_.postingEndAt)));
            } else if (SortBy.AMOUNT.equals(sortBy)) {
                query.orderBy(cb.desc(root.get(ProjectBid_.amountOfMoney)));
            }
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get(ProjectBid_.invalid), false));

            predicates.add(root.get(ProjectBid_.bidStatus).in(Arrays.asList(ProjectBid.BidStatus.CANCEL, ProjectBid.BidStatus.FAILED)));
            if (freelancerUserId != null) {
                predicates.add(cb.equal(root.get(ProjectBid_.participant), freelancerUserId));
            }
            if (projectType != null) {
                predicates.add(cb.equal(root.join(ProjectBid_.project).get(Project_.projectType), projectType));
            }

            Predicate[] predicatesArray = predicates.toArray(new Predicate[predicates.size()]);
            return cb.and(predicatesArray);
        });

    }

    public static Specification<ProjectBid> filterForCompletedProjectIncome(Long freelancerUserId, Long clientUserId) {
        return ((root, query, cb) -> {
            query.distinct(true);
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get(ProjectBid_.invalid), false));
            if (freelancerUserId != null) {
                predicates.add(cb.equal(root.get(ProjectBid_.participant), freelancerUserId));
            }
            if (clientUserId != null) {
                predicates.add(cb.equal(root.get(ProjectBid_.project).get(Project_.postingClient).get(User_.id), freelancerUserId));
            }
            predicates.add(cb.equal(root.get(ProjectBid_.bidStatus), ProjectBid.BidStatus.PICKED));
            predicates.add(cb.equal(root.get(ProjectBid_.project).get(Project_.status), Project.Status.COMPLETED));
            predicates.add(cb.equal(root.get(ProjectBid_.purchaseRecordDeleted), false));
            Predicate[] predicatesArray = predicates.toArray(new Predicate[predicates.size()]);
            return cb.and(predicatesArray);
        });

    }

    public static Specification<ProjectBid> filterForPayment(Long freelancerUserId, ProjectBid.BidType bidType) {
        return ((root, query, cb) -> {
            query.distinct(true);
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get(ProjectBid_.invalid), false));
            if (freelancerUserId != null) {
                predicates.add(cb.equal(root.get(ProjectBid_.participant), freelancerUserId));
            }
            if (bidType != null) {
                predicates.add(cb.equal(root.get(ProjectBid_.bidType), bidType));
            }
            predicates.add(cb.equal(root.get(ProjectBid_.purchaseRecordDeleted), false));
            Predicate[] predicatesArray = predicates.toArray(new Predicate[predicates.size()]);
            return cb.and(predicatesArray);
        });

    }

    public static Specification<ProjectBid> filterByPriority(Long projectId,
                                                             ProjectBid.BidType bidType,
                                                             ProjectBid.BidStatus status,
                                                             List<FreelancerProductItemType> includeProductTypes,
                                                             List<FreelancerProductItemType> excludeProductTypes) {
        return ((root, query, cb) -> {
            query.distinct(true);
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get(ProjectBid_.invalid), false));
            if (projectId != null) {
                predicates.add(cb.equal(root.get(ProjectBid_.project), projectId));
            }
            if (bidType != null) {
                predicates.add(cb.equal(root.get(ProjectBid_.bidType), bidType));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get(ProjectBid_.bidStatus), status));
            }
            if (includeProductTypes != null && includeProductTypes.size() > 0) {
                predicates.add(root.join(ProjectBid_.freelancerPayProducts).get(FreelancerPayProduct_.freelancerProductItemType).in(includeProductTypes));
            }
            if (excludeProductTypes != null && excludeProductTypes.size() > 0) {
                predicates.add(root.join(ProjectBid_.freelancerPayProducts).get(FreelancerPayProduct_.freelancerProductItemType).in(excludeProductTypes).not());
            }
            Predicate[] predicatesArray = predicates.toArray(new Predicate[predicates.size()]);
            return cb.and(predicatesArray);
        });

    }
}
