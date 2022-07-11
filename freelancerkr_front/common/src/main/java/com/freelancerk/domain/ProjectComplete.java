package com.freelancerk.domain;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class ProjectComplete {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    @ManyToOne
    private Project project;
    private boolean freelancerRequest;
    private boolean clientAccept;
    private LocalDateTime freelancerRequestAt;
    private LocalDateTime clientAcceptAt;
    private String memo;
    private boolean sendEndInTwoDaysMail;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
