package com.freelancerk.domain;


import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.time.LocalDateTime;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(ProjectProposition.class)
public class ProjectProposition_ {

    public static volatile SingularAttribute<ProjectProposition, User> freelancer;
    public static volatile SingularAttribute<ProjectProposition, Project> project;
    public static volatile SingularAttribute<ProjectProposition, ProjectProposition.Status> status;
    public static volatile SingularAttribute<ProjectProposition, LocalDateTime> createdAt;
}
