package com.freelancerk.domain;

import lombok.Data;

import javax.persistence.*;

import java.time.LocalDateTime;

import static javax.persistence.GenerationType.IDENTITY;


@Data
@Entity
public class PickMeUpMemo {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    @ManyToOne
    private PickMeUp pickMeUp;
    @Column(columnDefinition = "TEXT")
    private String content;
    @ManyToOne
    private User user;
    private LocalDateTime createdAt;
}
