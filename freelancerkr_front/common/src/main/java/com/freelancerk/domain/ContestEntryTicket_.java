package com.freelancerk.domain;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.time.LocalDateTime;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(ContestEntryTicket.class)
public class ContestEntryTicket_ {

    public static volatile SingularAttribute<ContestEntryTicket, Long> id;
    public static volatile SingularAttribute<ContestEntryTicket, FreelancerProductItemType> freelancerProductItemType;
    public static volatile SingularAttribute<ContestEntryTicket, ProjectBid> projectBid;
    public static volatile SingularAttribute<ContestEntryTicket, Boolean> invalid;
    public static volatile SingularAttribute<ContestEntryTicket, LocalDateTime> createdAt;
    public static volatile SingularAttribute<ContestEntryTicket, LocalDateTime> endAt;
}
