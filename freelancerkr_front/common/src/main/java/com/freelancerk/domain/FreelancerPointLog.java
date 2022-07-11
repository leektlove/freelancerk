package com.freelancerk.domain;

import lombok.Data;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class FreelancerPointLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private User user;
    @ManyToOne
    private Purchase purchase;
    @ManyToOne
    private Project contest;
    @ManyToOne
    private PickMeUp pickMeUp;

    private long priorPoints;
    private long afterPoints;
    private long addedPoint;
    private long usedPoint;
    private long remainPoint;

    private long usePoint;

    private String reason;

    @Enumerated(EnumType.STRING)
    private Type type;
    private LocalDateTime addedPointExpiredAt;
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Transient
    private String purchaseOptionDescription;

    public enum Type {
        CONTEST_ENTRY("컨테스트"), PICK_ME_UP("픽미업"), REFUND_FOR_CONTEST_ENTRY("컨테스트"), ETC("기타");

        @Getter
        private String desc;

        Type(String desc) {
            this.desc = desc;
        }
    }
}
