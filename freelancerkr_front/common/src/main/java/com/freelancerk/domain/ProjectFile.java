package com.freelancerk.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Data
@Entity
public class ProjectFile {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private Project project;
    private String fileUrl;
    private LocalDateTime createdAt;
}
