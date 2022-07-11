package com.freelancerk.domain;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class ContestRefund {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Project contest;
    @ManyToOne
    private User user;
    private int amountOfDeposit;
    private int amountOfPrize;
    private int amountOfVat;
    private int amount;
    private String type;
    private String reason;
    private LocalDateTime createdAt;
}
