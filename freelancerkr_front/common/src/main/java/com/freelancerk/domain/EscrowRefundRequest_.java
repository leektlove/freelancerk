package com.freelancerk.domain;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.time.LocalDateTime;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(EscrowRefundRequest.class)
public class EscrowRefundRequest_ {
    public static volatile SingularAttribute<EscrowRefundRequest, EscrowRefundRequest.Type> type;
    public static volatile SingularAttribute<EscrowRefundRequest, User> user;
    public static volatile SingularAttribute<EscrowRefundRequest, LocalDateTime> createdAt;
    public static volatile SingularAttribute<EscrowRefundRequest, LocalDateTime> refundedAt;
}
