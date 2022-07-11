package com.freelancerk.domain;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(ProjectCategory.class)
public class ProjectCategory_ {

    public static volatile SingularAttribute<ProjectCategory, Long> id;
    public static volatile SingularAttribute<ProjectCategory, Project> project;
    public static volatile SingularAttribute<ProjectCategory, Category> category;
}
