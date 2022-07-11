package com.freelancerk.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.freelancerk.TimeUtil;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class ContestEntryTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private User user;
    @JsonManagedReference
    @ManyToOne
    private ProjectBid projectBid;
    @ManyToOne
    private FreelancerProductItemType freelancerProductItemType;
    private int price;
    private boolean invalid;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    private LocalDateTime endAt;

    @Transient
    public String getValidationDateSpans() {
        return String.format("~ %s", TimeUtil.convertLocalDateTimeToStrWithTime(endAt));
    }
}
