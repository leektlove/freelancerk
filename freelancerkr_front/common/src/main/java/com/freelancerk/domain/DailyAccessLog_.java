package com.freelancerk.domain;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(DailyAccessLog.class)
public class DailyAccessLog_ {
    public static volatile SingularAttribute<DailyAccessLog, Long> id;
    public static volatile SingularAttribute<DailyAccessLog, User> user;
    public static volatile SingularAttribute<DailyAccessLog, LocalDate> date;
    public static volatile SingularAttribute<DailyAccessLog, LocalDateTime> createdAt;
}
