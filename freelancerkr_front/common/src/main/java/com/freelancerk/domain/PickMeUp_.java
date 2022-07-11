package com.freelancerk.domain;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.time.LocalDateTime;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(PickMeUp.class)
public class PickMeUp_ {
    public static volatile SingularAttribute<PickMeUp, Long> id;
    public static volatile SingularAttribute<PickMeUp, String> title;
    public static volatile SingularAttribute<PickMeUp, String> description;
    public static volatile SetAttribute<PickMeUp, Keyword> keywords;
    public static volatile SingularAttribute<PickMeUp, Category> category1st;
    public static volatile SingularAttribute<PickMeUp, Category> category2nd;
    public static volatile SingularAttribute<PickMeUp, Boolean> priority;
    public static volatile SingularAttribute<PickMeUp, Boolean> invalid;
    public static volatile SingularAttribute<PickMeUp, Boolean> purchaseRecordDeleted;
    public static volatile SingularAttribute<PickMeUp, Boolean> directDeal;
    public static volatile SingularAttribute<PickMeUp, Boolean> temp;
    public static volatile SingularAttribute<PickMeUp, User> user;
    public static volatile SingularAttribute<PickMeUp, LocalDateTime> createdAt;
    public static volatile ListAttribute<PickMeUp, FreelancerPayProduct> payProductList;
    public static volatile ListAttribute<PickMeUp, PickMeUpTicket> tickets;
}
