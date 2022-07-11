package com.freelancerk.domain;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class ProjectPointLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Project project;
    @ManyToOne
    private Purchase purchase;

    private int usePoint;
    private int addedPoint;

    private LocalDateTime addedPointExpiredAt;
    @ManyToOne
    private User user;
    private LocalDateTime createdAt;

    @Transient
    private String purchaseOptionDescription;
}
