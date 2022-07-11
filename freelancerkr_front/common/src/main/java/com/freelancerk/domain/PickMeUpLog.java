package com.freelancerk.domain;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class PickMeUpLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private User user;
    @ManyToOne
    private PickMeUp pickMeUp;

    @Column(name = "`desc`")
    private String desc;
    private String type;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
