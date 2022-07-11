package com.freelancerk.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.freelancerk.TimeUtil;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
public class PickMeUpTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private User user;
    @JsonManagedReference
    @ManyToOne
    private PickMeUp pickMeUp;
    @ManyToOne
    private FreelancerProductItemType freelancerProductItemType;
    private LocalDateTime startAt;
    private LocalDateTime endAt;

    private boolean sendExpirationNoti;
    private boolean sendExpirationInTwoDaysNoti;
    private boolean invalid;

    @Transient
    public String getValidationDateSpans() {
        return String.format("~ %s", TimeUtil.convertLocalDateTimeToStrWithTime(endAt));
    }
}
