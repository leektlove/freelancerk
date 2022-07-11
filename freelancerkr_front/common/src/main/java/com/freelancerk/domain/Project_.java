package com.freelancerk.domain;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.time.LocalDateTime;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Project.class)
public class Project_ {
    public static volatile SingularAttribute<Project, Long> id;
    public static volatile SingularAttribute<Project, String> title;
    public static volatile SingularAttribute<Project, String> description;
    public static volatile SingularAttribute<Project, Boolean> internalPriority;
    public static volatile SingularAttribute<Project, Boolean> externalPriority;
    public static volatile SingularAttribute<Project, Boolean> invalid;
    public static volatile SingularAttribute<Project, Boolean> purchaseRecordDeleted;
    public static volatile SingularAttribute<Project, Project.Type> projectType;
    public static volatile SingularAttribute<Project, Project.Status> status;
    public static volatile SingularAttribute<Project, Project.ExpectedPeriod> expectedPeriod;
    public static volatile SingularAttribute<Project, Project.PayCriteria> payCriteria;
    public static volatile SingularAttribute<Project, Project.WorkPlace> workPlace;
    public static volatile SingularAttribute<Project, Integer> numberOfApplyCount;
    public static volatile SingularAttribute<Project, Integer> maxBidAmount;
    public static volatile SingularAttribute<Project, Integer> minBidAmount;
    public static volatile SingularAttribute<Project, Integer> successBidPrice;
    public static volatile SingularAttribute<Project, Integer> budgetFrom;
    public static volatile SingularAttribute<Project, Integer> budgetTo;
    public static volatile SingularAttribute<Project, Integer> amountSortingCriteria;
    public static volatile SingularAttribute<Project, Integer> minPrize;
    public static volatile SingularAttribute<Project, User> postingClient;
    public static volatile SingularAttribute<Project, User> contractedFreelancer;
    public static volatile SingularAttribute<Project, Boolean> useEscrow;
    public static volatile SingularAttribute<Project, LocalDateTime> createdAt;
    public static volatile SingularAttribute<Project, LocalDateTime> updatedAt;
    public static volatile SingularAttribute<Project, LocalDateTime> postingStartAt;
    public static volatile SingularAttribute<Project, LocalDateTime> postingEndAt;
    public static volatile SingularAttribute<Project, LocalDateTime> completedAt;
    public static volatile SingularAttribute<Project, LocalDateTime> lastPurchasedAt;
    public static volatile SetAttribute<Project, ProjectItemTicket> projectItemTickets;
    public static volatile SetAttribute<Project, ProjectCategory> projectCategories;
}
