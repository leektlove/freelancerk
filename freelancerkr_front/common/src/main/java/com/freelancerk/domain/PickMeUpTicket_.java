package com.freelancerk.domain;


import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.time.LocalDateTime;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(PickMeUpTicket.class)
public class PickMeUpTicket_ {

    public static volatile SingularAttribute<PickMeUpTicket, Long> id;
    public static volatile SingularAttribute<PickMeUpTicket, FreelancerProductItemType> freelancerProductItemType;
    public static volatile SingularAttribute<PickMeUpTicket, PickMeUp> pickMeUp;
    public static volatile SingularAttribute<PickMeUpTicket, Boolean> invalid;
    public static volatile SingularAttribute<PickMeUpTicket, LocalDateTime> startAt;
    public static volatile SingularAttribute<PickMeUpTicket, LocalDateTime> endAt;
}
