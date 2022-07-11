package com.freelancerk.domain;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class AdvertisementHit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Advertisement advertisement;
    @ManyToOne
    private User user;
    @CreationTimestamp
    private LocalDateTime createdAt;
}
