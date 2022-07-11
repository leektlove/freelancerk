package com.freelancerk.domain;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class KpiFreelancer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private User user;
    private long numberOfAccessCount;
    private long numberOfProjectBids;
    private long numberOfContestEntries;
    private long numberOfProjectPropositionCount;
    private float ratings;
    private long totalTransactionAmount;
    private long totalTransactionCount;
    private long numberOfCompletedProject;
    private long numberOfOptionCount;
    private long totalChargedOptionPrice;
    private long points;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
