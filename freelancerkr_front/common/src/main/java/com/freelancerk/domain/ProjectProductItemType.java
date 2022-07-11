package com.freelancerk.domain;

import com.freelancerk.policy.PeriodUnit;
import com.freelancerk.policy.UsageType;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class ProjectProductItemType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Enumerated(EnumType.STRING)
    private Category category;
    private int mountOfMoneyUnit;
    private int originalMountOfMoney;
    private float discountRate;
    private int unitPeriod;
    @Enumerated(EnumType.STRING)
    private Code code;
    private String containsItemTypes;
    private boolean pack;
    @Enumerated(EnumType.STRING)
    private PeriodUnit periodUnit;
    @Enumerated(EnumType.STRING)
    private UsageType usageType;
    @Enumerated(EnumType.STRING)
    private Project.Type projectType;
    @Column(columnDefinition = "TEXT")
    private String description;
    private boolean priority;
    private boolean urgent;
    private boolean valid;

    public enum Category {
        INTERNAL, EXTERNAL
    }

    public enum Code {
        ESCROW, INTERNAL, INTERNAL_URGENT, INTERNAL_FEATURED, INTERNAL_PRIORITY, EXTERNAL, EXTERNAL_URGENT, EXTERNAL_FEATURED, EXTERNAL_PRIORITY,
        NDA_IP, ADVANCED_SMS, PRIORITY_PACK, EASY_PACK, LUXURY_PACK, SUPREME_PACK
    }
}
