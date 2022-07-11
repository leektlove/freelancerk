package com.freelancerk.domain;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Message.class)
public class Message_ {
    public static volatile SingularAttribute<Message, String> title;
    public static volatile SingularAttribute<Message, String> content;
    public static volatile SingularAttribute<Message, User> user;
}
