package com.freelancerk.domain;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Entity
public class DailyAccessLog {

    @GeneratedValue
    @Id
    private Long id;
    @ManyToOne
    private User user;
    private String remoteAddress;
    private String referer;
    private String userAgent;
    @Column(columnDefinition = "TIME")
    private LocalTime accessTime;
    private LocalDate date;
    private int count;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
