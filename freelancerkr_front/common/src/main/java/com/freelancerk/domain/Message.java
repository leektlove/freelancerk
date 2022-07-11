package com.freelancerk.domain;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Data
@Entity
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private User user;
    @Column(columnDefinition = "TEXT")
    private String content;
    @ManyToOne
    private Project project;
    @Column(name = "`read`")
    private boolean read = false;
    @CreationTimestamp
    private LocalDateTime createdAt;

    public String getPastTime() {
        if (LocalDateTime.now().isBefore(createdAt.plusHours(1))) {
            return String.format("%d분 전", createdAt.until(LocalDateTime.now(), ChronoUnit.MINUTES));
        } else if (LocalDateTime.now().isBefore(createdAt.plusDays(1))) {
            return String.format("%d시간 전", createdAt.until(LocalDateTime.now(), ChronoUnit.HOURS));
        } else if (LocalDateTime.now().isBefore(createdAt.plusMonths(1))) {
            return String.format("%d일 전", createdAt.until(LocalDateTime.now(), ChronoUnit.DAYS));
        } else if (LocalDateTime.now().isBefore(createdAt.plusYears(1))) {
            return String.format("%d달 전", createdAt.until(LocalDateTime.now(), ChronoUnit.MONTHS));
        } else {
            return String.format("%d년 전", createdAt.until(LocalDateTime.now(), ChronoUnit.YEARS));
        }
    }
}
