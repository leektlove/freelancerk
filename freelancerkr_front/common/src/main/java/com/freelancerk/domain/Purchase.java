package com.freelancerk.domain;

import lombok.Data;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class Purchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private User user;
    @Enumerated(EnumType.STRING)
    private Type type;
    private String chargedOptions;
    private int chargedOptionsAmountOfMoney;
    private int totalDiscountOptionPrice;
    private int usedPoints;
    private int numberOfOptions;
    private int supplyAmountOfMoney;
    private int vatAmountOfMoney;
    private int totalAmountOfMoney;
    private String impUid;
    private String merchantUid;
    private String receiptUrl;
    private String administrationId;
    @Enumerated(EnumType.STRING)
    private Status status;
    private boolean optionExtend;

    @ManyToOne
    private Project project;
    @ManyToOne
    private PickMeUp pickMeUp;
    @ManyToOne
    private ProjectBid projectBid;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Transient
    private boolean extend;
    @Transient
    private Long contestId;
    @Transient
    private long selectedPickMeUpId;

    @Transient
    private String chargedOptionsName;

    public enum Type {
        PICK_ME_UP("픽미업"), PROJECT("프로젝트"), PROJECT_BID("프로젝트 입찰"), CONTEST("컨테스트"), CONTEST_ENTRY("컨테스트 입찰");

        @Getter
        private String desc;

        Type(String desc) {
            this.desc = desc;
        }
    }

    public enum Status {
        REQUESTED, COMPLETED, REFUNDED
    }
}
