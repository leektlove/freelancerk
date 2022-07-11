package com.freelancerk.domain;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(KpiFreelancer.class)
public class KpiFreelancer_ {

    public static volatile SingularAttribute<KpiFreelancer, Long> id;
    public static volatile SingularAttribute<KpiFreelancer, User> user;
    public static volatile SingularAttribute<KpiFreelancer, Long> numberOfAccessCount;
    public static volatile SingularAttribute<KpiFreelancer, Long> numberOfProjectBids;
    public static volatile SingularAttribute<KpiFreelancer, Long> numberOfContestEntries;
    public static volatile SingularAttribute<KpiFreelancer, Long> numberOfProjectPropositionCount;
    public static volatile SingularAttribute<KpiFreelancer, Float> ratings;
    public static volatile SingularAttribute<KpiFreelancer, Long> totalTransactionAmount;
    public static volatile SingularAttribute<KpiFreelancer, Long> totalTransactionCount;
    public static volatile SingularAttribute<KpiFreelancer, Long> numberOfCompletedProject;
    public static volatile SingularAttribute<KpiFreelancer, Long> numberOfOptionCount;
    public static volatile SingularAttribute<KpiFreelancer, Long> totalChargedOptionPrice;
    public static volatile SingularAttribute<KpiFreelancer, Long> points;
}
