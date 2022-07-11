package com.freelancerk.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Data
@Entity
public class RewardLog {
    @Id
    private Long id;
    @ManyToOne
    private User user;
    private int amount;
    private LocalDateTime createdAt;
    private String reason;
}
