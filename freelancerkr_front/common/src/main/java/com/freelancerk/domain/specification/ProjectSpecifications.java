package com.freelancerk.domain.specification;

import com.freelancerk.domain.*;
import com.freelancerk.io.ContestOrdering;
import com.freelancerk.io.ProjectOrdering;
import com.freelancerk.model.SortBy;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class ProjectSpecifications {

    public static Specification<Project> filter(final Long clientUserId, final String keyword, final Project.Status status,
                                                final List<Project.Status> statuses, final Project.Type type) {
        return (root, query, cb) -> {
            query.distinct(true);
            query.orderBy(cb.asc(root.get(Project_.postingEndAt)));

            List<Predicate> predicates = new ArrayList<>();
            if (clientUserId != null) {
                predicates.add(cb.equal(root.join(Project_.postingClient).get(User_.id), clientUserId));
            }
            if (StringUtils.isNotEmpty(keyword)) {
                String newKeyword = keyword;
                if (newKeyword.startsWith("#")) {
                    newKeyword = keyword.substring(1, keyword.length());
                }
                List<Predicate> predicatesForKeyword = new ArrayList<>();
                predicatesForKeyword.add(cb.like(root.get(Project_.title), "%" + newKeyword + "%"));
                predicatesForKeyword.add(cb.like(root.get(Project_.description), "%" + newKeyword + "%"));
                predicatesForKeyword.add(cb.like(root.join(Project_.projectCategories, JoinType.LEFT).get(ProjectCategory_.category).get(Category_.name), "%" + newKeyword + "%"));
                predicates.add(cb.or(predicatesForKeyword.toArray(new Predicate[predicatesForKeyword.size()])));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get(Project_.status), status));
            }
            if (statuses != null && !statuses.isEmpty()) {
                predicates.add(root.get(Project_.status).in(Arrays.asList(statuses)));
            }
            if (type != null) {
                predicates.add(cb.equal(root.get(Project_.projectType), type));
            }
            if (Project.Status.TEMP.equals(status)) {
                predicates.add(cb.greaterThanOrEqualTo(root.get(Project_.updatedAt), LocalDateTime.now().minusMonths(1)));
            }
            predicates.add(cb.equal(root.get(Project_.invalid), false));
            Predicate[] predicatesArray = predicates.toArray(new Predicate[predicates.size()]);
            return cb.and(predicatesArray);
        };
    }

    public static Specification<Project> filterForFreelancer(final Long freelancerUserId, final String keyword,
                                                final List<Project.Status> statuses, final Project.Type type) {
        return (root, query, cb) -> {
            query.distinct(true);
            query.orderBy(cb.asc(root.get(Project_.postingEndAt)));

            List<Predicate> predicates = new ArrayList<>();
            if (freelancerUserId != null) {
                predicates.add(cb.equal(root.join(Project_.contractedFreelancer).get(User_.id), freelancerUserId));
            }
            if (StringUtils.isNotEmpty(keyword)) {
                String newKeyword = keyword;
                if (newKeyword.startsWith("#")) {
                    newKeyword = keyword.substring(1, keyword.length());
                }
                List<Predicate> predicatesForKeyword = new ArrayList<>();
                predicatesForKeyword.add(cb.like(root.get(Project_.title), "%" + newKeyword + "%"));
                predicatesForKeyword.add(cb.like(root.get(Project_.description), "%" + newKeyword + "%"));
                predicatesForKeyword.add(cb.like(root.join(Project_.projectCategories, JoinType.LEFT).get(ProjectCategory_.category).get(Category_.name), "%" + newKeyword + "%"));
                predicates.add(cb.or(predicatesForKeyword.toArray(new Predicate[predicatesForKeyword.size()])));
            }
            if (statuses != null && !statuses.isEmpty()) {
                predicates.add(root.get(Project_.status).in(Arrays.asList(statuses)));
            }
            if (type != null) {
                predicates.add(cb.equal(root.get(Project_.projectType), type));
            }
            predicates.add(cb.equal(root.get(Project_.invalid), false));
            Predicate[] predicatesArray = predicates.toArray(new Predicate[predicates.size()]);
            return cb.and(predicatesArray);
        };
    }

    public static Specification<Project> filterSpecificSearch(final String keyword, final Project.Status status,
                                                              final Project.Type projectType,
                                                              final Long categoryId, SortBy sortBy,
                                                              final ProjectProductItemType productItemUrgentItemType,
                                                              final List<ProjectProductItemType> containItems, Set<Category> userCategories) {
        return (root, query, cb) -> {
            query.distinct(true);
            List<Order> orders = new ArrayList<>();
            orders.add(
                    cb.desc(cb.selectCase().when(cb.equal(root.get(Project_.internalPriority), true), 2).otherwise(1))

            );
            if (sortBy != null) {

                if (SortBy.KEYWORDS.equals(sortBy) && productItemUrgentItemType != null) {
                    if (userCategories != null && !userCategories.isEmpty()) {
                        for (Category category: userCategories) {
                            orders.add(cb.desc(cb.selectCase().when(root.join(Project_.projectCategories).get(ProjectCategory_.category).in(category.getParentCategory()), 2).otherwise(1)));
                        }
                    }
                    orders.add(
                            cb.desc(root.get(Project_.createdAt))
                    );
                } else if (SortBy.URGENT.equals(sortBy) && productItemUrgentItemType != null) {
                    orders.add(cb.desc(cb.selectCase().when(root.join(Project_.projectItemTickets, JoinType.LEFT).get(ProjectItemTicket_.projectProductItemType).in(productItemUrgentItemType), 2).otherwise(1)));
                } else if (SortBy.CREATED_AT.equals(sortBy)) {
                    orders.add(cb.desc(root.get(Project_.createdAt)));
                } else if (SortBy.POSTING_END_AT.equals(sortBy)) {
                    orders.add(cb.asc(root.get(Project_.postingEndAt)));
                } else if (SortBy.AMOUNT.equals(sortBy)) {
                    orders.add(cb.desc(root.get(Project_.amountSortingCriteria)));
                }
            }
//            orders.add(cb.desc(cb.selectCase().when(cb.equal(root.get(Project_.projectType), Project.Type.CONTEST), 2).otherwise(1)));
            query.orderBy(orders);

            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.isNotEmpty(keyword)) {
                String newKeyword = keyword;
                if (newKeyword.startsWith("#")) {
                    newKeyword = keyword.substring(1, newKeyword.length());
                }
                List<Predicate> predicatesForKeyword = new ArrayList<>();
                predicatesForKeyword.add(cb.like(root.get(Project_.title), "%" + newKeyword + "%"));
                predicatesForKeyword.add(cb.like(root.get(Project_.description), "%" + newKeyword + "%"));
                predicatesForKeyword.add(cb.like(root.join(Project_.projectCategories, JoinType.LEFT).get(ProjectCategory_.category).get(Category_.name), "%" + newKeyword + "%"));
                predicates.add(cb.or(predicatesForKeyword.toArray(new Predicate[predicatesForKeyword.size()])));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get(Project_.status), status));
            }
            if (projectType != null) {
                predicates.add(cb.equal(root.get(Project_.projectType), projectType));
            }
            if (categoryId != null) {
                predicates.add(cb.equal(root.join(Project_.projectCategories, JoinType.LEFT).join(ProjectCategory_.category).join(Category_.parentCategory).get(Category_.parentCategory).get(Category_.id), categoryId));
            }
            if (containItems != null && containItems.size() > 0) {
                predicates.add(cb.greaterThan(root.join(Project_.projectItemTickets, JoinType.LEFT).get(ProjectItemTicket_.expiredAt), LocalDateTime.now()));
                predicates.add(root.join(Project_.projectItemTickets, JoinType.LEFT).get(ProjectItemTicket_.projectProductItemType).in(containItems));
            }

            predicates.add(cb.greaterThan(root.get(Project_.postingEndAt), LocalDateTime.now()));
//            if (premium) {
//                List<Predicate> predicatesForType = new ArrayList<>();
//                predicatesForType.add(root.join(Project_.projectItemTickets, JoinType.LEFT).get(ProjectItemTicket_.projectProductItemType).in(projectProductItemPremiumTypes));
//                predicatesForType.add(root.join(Project_.contestItemTickets, JoinType.LEFT).get(ContestItemTicket_.contestProductItemType).in(contestProductItemPremiumTypes));
//                Predicate[] predicatesForTypeArray = predicatesForType.toArray(new Predicate[predicatesForType.size()]);
//                predicates.add(cb.or(predicatesForTypeArray));
//            } else {
//                List<Predicate> predicatesForType = new ArrayList<>();
//                predicatesForType.add(root.join(Project_.projectItemTickets, JoinType.LEFT).get(ProjectItemTicket_.projectProductItemType).in(projectProductItemPremiumTypes).not());
//                predicatesForType.add(root.join(Project_.contestItemTickets, JoinType.LEFT).get(ContestItemTicket_.contestProductItemType).in(contestProductItemPremiumTypes).not());
//                Predicate[] predicatesForTypeArray = predicatesForType.toArray(new Predicate[predicatesForType.size()]);
//                predicates.add(cb.or(predicatesForTypeArray));
//            }

            predicates.add(cb.equal(root.get(Project_.invalid), false));
            Predicate[] predicatesArray = predicates.toArray(new Predicate[predicates.size()]);
            return cb.and(predicatesArray);
        };
    }

    public static Specification<Project> filterForAdmin(ProjectOrdering orderBy, Sort.Direction direction,
                                                              final String keyword,
                                                              final Long categoryId,
                                                              final LocalDateTime createdFrom,
                                                              final LocalDateTime createdTo,
                                                              final String projectKeyword,
                                                              final Project.Status projectStatus,
                                                              final Integer bidCountFrom,
                                                              final Integer bidCountTo,
                                                              final Integer successBidAmountFrom,
                                                              final Integer successBidAmountTo,
                                                              final Project.ExpectedPeriod expectedPeriod,
                                                              final Integer budgetFrom,
                                                              final Integer budgetTo,
                                                              final Project.PayCriteria payCriteria,
                                                              final Project.WorkPlace workPlace,
                                                              final LocalDateTime postingStartFrom,
                                                              final LocalDateTime postingEndTo,
                                                              final Boolean useEscrow) {
        return (root, query, cb) -> {
            query.distinct(true);
            if (orderBy != null) {
                if (ProjectOrdering.CREATED_AT.equals(orderBy)) {
                    if (Sort.Direction.DESC.equals(direction)) {
                        query.orderBy(cb.desc(root.get(Project_.createdAt)));
                    } else {
                        query.orderBy(cb.asc(root.get(Project_.createdAt)));
                    }
                } else if (ProjectOrdering.BID_COUNT.equals(orderBy)) {
                    if (Sort.Direction.DESC.equals(direction)) {
                        query.orderBy(cb.desc(root.get(Project_.numberOfApplyCount)));
                    } else {
                        query.orderBy(cb.asc(root.get(Project_.numberOfApplyCount)));
                    }
                } else if (ProjectOrdering.MAX_BID_AMOUNT.equals(orderBy)) {
                    if (Sort.Direction.DESC.equals(direction)) {
                        query.orderBy(cb.desc(root.get(Project_.maxBidAmount)));
                    } else {
                        query.orderBy(cb.asc(root.get(Project_.maxBidAmount)));
                    }
                } else if (ProjectOrdering.MIN_BID_AMOUNT.equals(orderBy)) {
                    if (Sort.Direction.DESC.equals(direction)) {
                        query.orderBy(cb.desc(root.get(Project_.minBidAmount)));
                    } else {
                        query.orderBy(cb.asc(root.get(Project_.minBidAmount)));
                    }
                } else if (ProjectOrdering.SUCCESS_BID_AMOUNT.equals(orderBy)) {
                    if (Sort.Direction.DESC.equals(direction)) {
                        query.orderBy(cb.desc(root.get(Project_.successBidPrice)));
                    } else {
                        query.orderBy(cb.asc(root.get(Project_.successBidPrice)));
                    }
                } else if (ProjectOrdering.USE_ESCROW.equals(orderBy)) {
                    if (Sort.Direction.DESC.equals(direction)) {
                        query.orderBy(cb.desc(root.get(Project_.useEscrow)));
                    } else {
                        query.orderBy(cb.asc(root.get(Project_.useEscrow)));
                    }
                } else if (ProjectOrdering.ESCROW_REMAIN_AMOUNT.equals(orderBy)) {
                    if (Sort.Direction.DESC.equals(direction)) {
                        query.orderBy(cb.desc(root.get(Project_.useEscrow))); // todo
                    } else {
                        query.orderBy(cb.asc(root.get(Project_.useEscrow))); // todo
                    }
                }
            } else {
                query.orderBy(cb.desc(root.get(Project_.id)));
            }

            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.isNotEmpty(keyword)) {
                String newKeyword = keyword;
                if (newKeyword.startsWith("#")) {
                    newKeyword = keyword.substring(1, newKeyword.length());
                }
                List<Predicate> predicatesForKeyword = new ArrayList<>();
                predicatesForKeyword.add(cb.like(root.get(Project_.postingClient).get(User_.name), "%" + newKeyword + "%"));
                predicatesForKeyword.add(cb.like(root.get(Project_.postingClient).get(User_.nickname), "%" + newKeyword + "%"));
                predicatesForKeyword.add(cb.like(root.get(Project_.postingClient).get(User_.corporateName), "%" + newKeyword + "%"));
                predicatesForKeyword.add(cb.like(root.get(Project_.postingClient).get(User_.cellphone), "%" + newKeyword + "%"));
                predicatesForKeyword.add(cb.like(root.get(Project_.postingClient).get(User_.email), "%" + newKeyword + "%"));
                predicatesForKeyword.add(cb.like(root.get(Project_.title), "%" + newKeyword + "%"));
                predicatesForKeyword.add(cb.like(root.get(Project_.description), "%" + newKeyword + "%"));
                predicates.add(cb.or(predicatesForKeyword.toArray(new Predicate[predicatesForKeyword.size()])));
            }
            predicates.add(root.get(Project_.projectType).in(Arrays.asList(Project.Type.PROJECT, Project.Type.CONTEST_TO_PROJECT)));
            predicates.add(root.get(Project_.status).in(Arrays.asList(Project.Status.VOLATILE, Project.Status.TEMP)).not());

            if (categoryId != null) {
                predicates.add(cb.equal(root.join(Project_.projectCategories, JoinType.LEFT).get(ProjectCategory_.category).get(Category_.id), categoryId));
            }
            if (createdFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get(Project_.createdAt), createdFrom));
            }
            if (createdTo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get(Project_.createdAt), createdTo));
            }
            if (StringUtils.isNotEmpty(projectKeyword)) {
                predicates.add(cb.like(root.join(Project_.projectCategories, JoinType.LEFT).get(ProjectCategory_.category).get(Category_.name), "%" + keyword + "%"));
            }
            if (useEscrow != null) {
                predicates.add(cb.equal(root.get(Project_.useEscrow), useEscrow));
            }
            if (projectStatus != null) {
                predicates.add(cb.equal(root.get(Project_.status), projectStatus));
            } else {
                predicates.add(cb.notEqual(root.get(Project_.status), Project.Status.VOLATILE));
            }
            if (payCriteria != null) {
                predicates.add(cb.equal(root.get(Project_.payCriteria), payCriteria));
            }
            if (expectedPeriod != null) {
                predicates.add(cb.equal(root.get(Project_.expectedPeriod), expectedPeriod));
            }
            if (workPlace != null) {
                predicates.add(cb.equal(root.get(Project_.workPlace), workPlace));
            }
            if (createdFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get(Project_.createdAt), createdFrom));
            }
            if (createdTo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get(Project_.createdAt), createdTo));
            }
            if (bidCountFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get(Project_.numberOfApplyCount), bidCountFrom));
            }
            if (bidCountTo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get(Project_.numberOfApplyCount), bidCountTo));
            }
            if (successBidAmountFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get(Project_.successBidPrice), successBidAmountFrom));
            }
            if (successBidAmountTo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get(Project_.successBidPrice), successBidAmountTo));
            }
            if (budgetFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get(Project_.budgetFrom), budgetFrom));
            }
            if (budgetTo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get(Project_.budgetTo), budgetTo));
            }
            if (postingStartFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get(Project_.createdAt), postingStartFrom));
            }
            if (postingEndTo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get(Project_.createdAt), postingEndTo));
            }

            Predicate[] predicatesArray = predicates.toArray(new Predicate[predicates.size()]);
            return cb.and(predicatesArray);
        };
    }

    public static Specification<Project> filterContestForAdmin(ContestOrdering orderBy, Sort.Direction direction,
                                                               final String keyword,
                                                               final Long categoryId,
                                                               final LocalDateTime createdFrom,
                                                               final LocalDateTime createdTo,
                                                               final Project.Status projectStatus,
                                                               final Integer bidCountFrom,
                                                               final Integer bidCountTo,
                                                               final Integer totalPrizeFrom,
                                                               final Integer totalPrizeTo) {
        return (root, query, cb) -> {
            query.distinct(true);
            if (orderBy != null) {
                if (ContestOrdering.CREATED_AT.equals(orderBy)) {
                    if (Sort.Direction.DESC.equals(direction)) {
                        query.orderBy(cb.desc(root.get(Project_.createdAt)));
                    } else {
                        query.orderBy(cb.asc(root.get(Project_.createdAt)));
                    }
                } else if (ContestOrdering.BID_COUNT.equals(orderBy)) {
                    if (Sort.Direction.DESC.equals(direction)) {
                        query.orderBy(cb.desc(root.get(Project_.numberOfApplyCount)));
                    } else {
                        query.orderBy(cb.asc(root.get(Project_.numberOfApplyCount)));
                    }
                } else if (ContestOrdering.START_AT.equals(orderBy)) {
                    if (Sort.Direction.DESC.equals(direction)) {
                        query.orderBy(cb.desc(root.get(Project_.postingStartAt)));
                    } else {
                        query.orderBy(cb.asc(root.get(Project_.postingStartAt)));
                    }
                } else if (ContestOrdering.POSTING_END_AT.equals(orderBy)) {
                    if (Sort.Direction.DESC.equals(direction)) {
                        query.orderBy(cb.desc(root.get(Project_.postingEndAt)));
                    } else {
                        query.orderBy(cb.asc(root.get(Project_.postingEndAt)));
                    }
                } else if (ContestOrdering.COMPLETED_AT.equals(orderBy)) {
                    if (Sort.Direction.DESC.equals(direction)) {
                        query.orderBy(cb.desc(root.get(Project_.completedAt)));
                    } else {
                        query.orderBy(cb.asc(root.get(Project_.completedAt)));
                    }
                } else if (ContestOrdering.TOTAL_PRIZE.equals(orderBy)) {
                    if (Sort.Direction.DESC.equals(direction)) {
                        query.orderBy(cb.desc(root.get(Project_.minPrize)));
                    } else {
                        query.orderBy(cb.asc(root.get(Project_.minPrize)));
                    }
                } else if (ContestOrdering.PURCHASE_AMOUNT.equals(orderBy)) {
                    if (Sort.Direction.DESC.equals(direction)) {
                        query.orderBy(cb.desc(root.get(Project_.minPrize))); // todo
                    } else {
                        query.orderBy(cb.asc(root.get(Project_.minPrize))); // todo
                    }
                }
            } else {
                query.orderBy(cb.desc(root.get(Project_.id)));
            }

            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.isNotEmpty(keyword)) {
                String newKeyword = keyword;
                if (newKeyword.startsWith("#")) {
                    newKeyword = keyword.substring(1, newKeyword.length());
                }
                List<Predicate> predicatesForKeyword = new ArrayList<>();
                predicatesForKeyword.add(cb.like(root.get(Project_.postingClient).get(User_.name), "%" + newKeyword + "%"));
                predicatesForKeyword.add(cb.like(root.get(Project_.postingClient).get(User_.nickname), "%" + newKeyword + "%"));
                predicatesForKeyword.add(cb.like(root.get(Project_.postingClient).get(User_.corporateName), "%" + newKeyword + "%"));
                predicatesForKeyword.add(cb.like(root.get(Project_.postingClient).get(User_.cellphone), "%" + newKeyword + "%"));
                predicatesForKeyword.add(cb.like(root.get(Project_.postingClient).get(User_.email), "%" + newKeyword + "%"));
                predicatesForKeyword.add(cb.like(root.get(Project_.title), "%" + newKeyword + "%"));
                predicatesForKeyword.add(cb.like(root.get(Project_.description), "%" + newKeyword + "%"));
                predicates.add(cb.or(predicatesForKeyword.toArray(new Predicate[predicatesForKeyword.size()])));
            }
            predicates.add(root.get(Project_.projectType).in(Arrays.asList(Project.Type.CONTEST)));
            predicates.add(root.get(Project_.status).in(Arrays.asList(Project.Status.VOLATILE, Project.Status.TEMP)).not());

            if (createdFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get(Project_.createdAt), createdFrom));
            }
            if (createdTo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get(Project_.createdAt), createdTo));
            }
            if (categoryId != null) {
                predicates.add(cb.equal(root.join(Project_.projectCategories, JoinType.LEFT).get(ProjectCategory_.category).get(Category_.id), categoryId));
            }
            if (projectStatus != null) {
                predicates.add(cb.equal(root.get(Project_.status), projectStatus));
            }
            if (createdFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get(Project_.createdAt), createdFrom));
            }
            if (createdTo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get(Project_.createdAt), createdTo));
            }
            if (bidCountFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get(Project_.numberOfApplyCount), bidCountFrom));
            }
            if (bidCountTo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get(Project_.numberOfApplyCount), bidCountTo));
            }
            if (totalPrizeFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get(Project_.minPrize), totalPrizeFrom)); // todo
            }
            if (totalPrizeTo != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get(Project_.minPrize), totalPrizeTo)); // todo
            }

            Predicate[] predicatesArray = predicates.toArray(new Predicate[predicates.size()]);
            return cb.and(predicatesArray);
        };
    }
}
