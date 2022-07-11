package com.freelancerk.domain;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class ContestComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Project contest;

    @ManyToOne
    private ContestComment parentComment;
    @Column(columnDefinition = "TEXT")
    private String content;
    private LocalDateTime createdAt;

    public enum Type {

    }
}
