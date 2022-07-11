package com.freelancerk.domain;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class InquiryAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Inquiry inquiry;
    @Column(columnDefinition = "TEXT")
    private String content;
    private LocalDateTime createdAt;
    @ManyToOne
    private AdminUser adminUser;
}
