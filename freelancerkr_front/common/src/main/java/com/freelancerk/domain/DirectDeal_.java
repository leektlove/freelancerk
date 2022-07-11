package com.freelancerk.domain;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.time.LocalDateTime;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(DirectDeal.class)
public class DirectDeal_ {
    public static volatile SingularAttribute<DirectDeal, Long> id;
    public static volatile SingularAttribute<DirectDeal, User> user;
    public static volatile SingularAttribute<DirectDeal, PickMeUp> pickMeUp;
    public static volatile SingularAttribute<DirectDeal, LocalDateTime> createdAt;
}
