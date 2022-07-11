package com.freelancerk.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class ProjectRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private User raterUser;
    @ManyToOne
    private User ratedUser;
    @JsonIgnore
    @ManyToOne
    private Project project;
    @Enumerated(EnumType.STRING)
    private RaterType raterType;

    private int type1Rate;
    private int type2Rate;
    private int type3Rate;
    private int type4Rate;
    private int type5Rate;
    @Column(columnDefinition = "TEXT")
    private String content;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime udpatedAt;

    public enum RaterType {
        FREELANCER, CLIENT
    }
}
