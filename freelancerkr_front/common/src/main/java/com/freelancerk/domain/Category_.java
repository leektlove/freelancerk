package com.freelancerk.domain;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Category.class)
public class Category_ {

    public static volatile SingularAttribute<Category, Long> id;
    public static volatile SingularAttribute<Category, String> name;
    public static volatile SingularAttribute<Category, Category> parentCategory;
    public static volatile SingularAttribute<Category, Category> topCategory;
}
