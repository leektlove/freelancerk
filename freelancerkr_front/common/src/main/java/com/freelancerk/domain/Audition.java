package com.freelancerk.domain;

import lombok.Data;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
public class Audition {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String auditionId;
    private String name;
    private String text;
    private String imageUrl;
    private String status;
    
    private LocalDateTime auditionAt;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    @Transient
    private List<Audition> list;
    @Transient
    private int listCnt;
}
