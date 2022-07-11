package com.freelancerk.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.freelancerk.TimeUtil;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class ContestEntryTicketLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JsonBackReference
    @ManyToOne
    private ProjectBid projectBid;
    @JsonIgnore
    @JsonBackReference
    @ManyToOne
    private Purchase purchase;
    @ManyToOne
    private FreelancerProductItemType freelancerProductItemType;
    private int price;
    private boolean invalid;
    private LocalDateTime expiredAt;
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Transient
    public String getValidationDateSpans() {
        return String.format("~ %s", TimeUtil.convertLocalDateTimeToStrWithTime(expiredAt));
    }
}
