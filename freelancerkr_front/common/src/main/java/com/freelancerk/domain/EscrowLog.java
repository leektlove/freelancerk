package com.freelancerk.domain;

import com.freelancerk.type.PaymentMethod;
import lombok.Data;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class EscrowLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private User depositUser;
    @ManyToOne
    private User withdrawalUser;
    @ManyToOne
    private Project project;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    private LocalDateTime depositAt;
    private int amount;
    private int amountWhVat;
    @Enumerated(EnumType.STRING)
    private Type type;
    @Column(columnDefinition = "TEXT")
    private String memoFromAdmin;
    private String mid;
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    public enum Type {
        PENDING("대기"), DEPOSIT("예금"), WITHDRAWAL("출금");

        @Getter
        private String desc;

        Type(String desc) {
            this.desc = desc;
        }
    }
}
