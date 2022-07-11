package com.freelancerk.domain;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class KpiClient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private User user;
    private long numberOfAccessCount;
    private long numberOfProjectCount;
    private long numberOfContestCount;
    private long numberOfProjectPropositionCount;
    private long numberOfDirectDealCount;
    private float ratings;
    private long totalTransactionAmount;
    private long numberOfCompletedProject;
    private long numberOfOptionCount;
    private long totalChargedOptionPrice;
    private long points;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
