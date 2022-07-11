package com.freelancerk.domain;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

import java.time.LocalDateTime;

@Data
@Entity
public class ClientPointLog {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    @ManyToOne
    private User user;
    private long priorPoints;
    private long afterPoints;
    private int amount;
    private long addedPoint;
    private long usedPoint;
    private long remainPoint;
    private long usePoint;
    @ManyToOne
    private Project project;
    @ManyToOne
    private Purchase purchase;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    private LocalDateTime addedPointExpiredAt;
    private String reason;

    @Transient
    public String purchaseOptionDescription;

    public boolean isUsed() {
        return priorPoints > afterPoints;
    }
}
