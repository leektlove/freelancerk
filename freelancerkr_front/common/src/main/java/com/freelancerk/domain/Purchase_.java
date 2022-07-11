package com.freelancerk.domain;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Purchase.class)
public class Purchase_ {

    public static volatile SingularAttribute<Purchase, Long> id;
    public static volatile SingularAttribute<Purchase, Purchase.Status> status;
}
