package com.freelancerk.domain;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.time.LocalDateTime;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(User.class)
public class User_ {
    public static volatile SingularAttribute<User, Long> id;
    public static volatile SetAttribute<User, Category> categories;
    public static volatile SingularAttribute<User, String> name;
    public static volatile SingularAttribute<User, String> nickname;
    public static volatile SingularAttribute<User, String> corporateName;
    public static volatile SingularAttribute<User, String> email;
    public static volatile SingularAttribute<User, String> cellphone;
    public static volatile SingularAttribute<User, String> roles;
    public static volatile SingularAttribute<User, BankType> bankForReceivingPayment;
    public static volatile SingularAttribute<User, String> bankAccountName;
    public static volatile SingularAttribute<User, String> bankAccountForReceivingPayment;
    public static volatile SingularAttribute<User, String> fpUser;
    public static volatile SingularAttribute<User, User.BusinessType> businessType;
    public static volatile SingularAttribute<User, Boolean> useEscrow;
    public static volatile SingularAttribute<User, Boolean> leaved;
    public static volatile SingularAttribute<User, LocalDateTime> createdAt;
}
