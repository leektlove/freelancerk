package com.freelancerk.domain;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.time.LocalDateTime;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(ProjectBid.class)
public class ProjectBid_ {

    public static volatile SingularAttribute<ProjectBid, Long> id;
    public static volatile SingularAttribute<ProjectBid, Project> project;
    public static volatile SingularAttribute<ProjectBid, User> participant;
    public static volatile SingularAttribute<ProjectBid, ProjectBid.BidType> bidType;
    public static volatile SingularAttribute<ProjectBid, ProjectBid.BidStatus> bidStatus;
    public static volatile SingularAttribute<ProjectBid, Integer> amountOfMoney;
    public static volatile SingularAttribute<ProjectBid, LocalDateTime> applyAt;
    public static volatile SingularAttribute<ProjectBid, Boolean> invalid;
    public static volatile SingularAttribute<ProjectBid, Boolean> purchaseRecordDeleted;
    public static volatile SetAttribute<ProjectBid, FreelancerPayProduct> freelancerPayProducts;
}
