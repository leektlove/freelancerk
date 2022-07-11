package com.freelancerk.domain;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.time.LocalDateTime;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(ProjectFavorite.class)
public class ProjectFavorite_ {

    public static volatile SingularAttribute<ProjectFavorite, Long> id;
    public static volatile SingularAttribute<ProjectFavorite, User> user;
    public static volatile SingularAttribute<ProjectFavorite, Project> project;
    public static volatile SingularAttribute<ProjectFavorite, LocalDateTime> createdAt;
}
