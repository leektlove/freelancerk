package com.freelancerk.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.freelancerk.TimeUtil;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
public class PickMeUpTicketLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private PickMeUp pickMeUp;
    @JsonIgnore
    @JsonBackReference
    @ManyToOne
    private Purchase purchase;
    @ManyToOne
    private FreelancerProductItemType freelancerProductItemType;
    private int optionPrice;
    private int optionCount;

    private LocalDateTime expiredAt;
    @CreationTimestamp
    private LocalDateTime createdAt;
    private boolean invalid;

    @Transient
    public String getValidationDateSpans() {
        return String.format("~ %s", TimeUtil.convertLocalDateTimeToStrWithTime(expiredAt));
    }
}
