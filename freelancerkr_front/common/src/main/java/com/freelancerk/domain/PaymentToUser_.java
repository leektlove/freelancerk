package com.freelancerk.domain;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.time.LocalDateTime;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(PaymentToUser.class)
public class PaymentToUser_ {
    public static volatile SingularAttribute<PaymentToUser, Long> id;
    public static volatile SingularAttribute<PaymentToUser, Project> project;
    public static volatile SingularAttribute<PaymentToUser, PaymentToUser.Status> status;
    public static volatile SingularAttribute<PaymentToUser, LocalDateTime> createdAt;
    public static volatile SingularAttribute<PaymentToUser, LocalDateTime> depositAt;
}
