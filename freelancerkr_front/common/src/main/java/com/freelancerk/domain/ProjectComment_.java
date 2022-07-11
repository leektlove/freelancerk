package com.freelancerk.domain;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(ProjectComment.class)
public class ProjectComment_ {

    public static volatile SingularAttribute<ProjectComment, Long> id;
    public static volatile SingularAttribute<ProjectComment, Project> project;
    public static volatile SingularAttribute<ProjectComment, User> user;
    public static volatile SingularAttribute<ProjectComment, User> targetUser;
}
