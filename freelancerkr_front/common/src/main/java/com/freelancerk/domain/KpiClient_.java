package com.freelancerk.domain;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(KpiClient.class)
public class KpiClient_ {

    public static volatile SingularAttribute<KpiClient, Long> id;
    public static volatile SingularAttribute<KpiClient, User> user;
    public static volatile SingularAttribute<KpiClient, Long> numberOfAccessCount;
    public static volatile SingularAttribute<KpiClient, Long> numberOfProjectCount;
    public static volatile SingularAttribute<KpiClient, Long> numberOfContestCount;
    public static volatile SingularAttribute<KpiClient, Long> numberOfProjectPropositionCount;
    public static volatile SingularAttribute<KpiClient, Long> numberOfDirectDealCount;
    public static volatile SingularAttribute<KpiClient, Float> ratings;
    public static volatile SingularAttribute<KpiClient, Long> totalTransactionAmount;
    public static volatile SingularAttribute<KpiClient, Long> numberOfCompletedProject;
    public static volatile SingularAttribute<KpiClient, Long> numberOfOptionCount;
    public static volatile SingularAttribute<KpiClient, Long> totalChargedOptionPrice;
    public static volatile SingularAttribute<KpiClient, Long> points;
}
