package com.freelancerk.domain;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.time.LocalDateTime;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(ProjectItemTicket.class)
public class ProjectItemTicket_ {

    public static volatile SingularAttribute<ProjectItemTicket, Long> id;
    public static volatile SingularAttribute<ProjectItemTicket, Project> project;
    public static volatile SingularAttribute<ProjectItemTicket, ProjectProductItemType> projectProductItemType;
    public static volatile SingularAttribute<ProjectItemTicket, LocalDateTime> createdAt;
    public static volatile SingularAttribute<ProjectItemTicket, LocalDateTime> expiredAt;
}
