package com.freelancerk.domain;

import lombok.Data;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class EscrowRefundRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private User user;
    private int amount;
    private String reason;
    private String cancelReason;
    private String accountName;
    private String accountBank;
    private String accountNo;

    @Enumerated(EnumType.STRING)
    private User.BusinessType businessType;

    private boolean cancelReceipt;
    private boolean delayedTransfer;

    @Enumerated(EnumType.STRING)
    private Type type;
    @CreationTimestamp
    private LocalDateTime createdAt;
    private LocalDateTime refundedAt;
    private LocalDateTime cancelledAt;

    @Transient
    public int getVat() {
        return (int) (amount*0.1f);
    }

    @Transient
    public int getSupplyAmount() {
        return amount - getVat();
    }

    public enum Type {
        REQUEST("신청"), PROCESSED("처리됨"), CANCELLED("취소됨");

        @Getter
        private String desc;

        Type(String desc) {
            this.desc = desc;
        }
    }
}
