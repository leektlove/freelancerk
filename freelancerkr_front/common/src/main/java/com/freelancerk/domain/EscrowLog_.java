package com.freelancerk.domain;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.time.LocalDateTime;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(EscrowLog.class)
public class EscrowLog_ {
    public static volatile SingularAttribute<EscrowLog, EscrowLog.Type> type;
    public static volatile SingularAttribute<EscrowLog, LocalDateTime> createdAt;
    public static volatile SingularAttribute<EscrowLog, Project> project;
    public static volatile SingularAttribute<EscrowLog, User> withdrawalUser;
    public static volatile SingularAttribute<EscrowLog, User> depositUser;
}
