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
public class ProjectItemTicketLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JsonBackReference
    @ManyToOne
    private Project project;
    @JsonIgnore
    @JsonBackReference
    @ManyToOne
    private Purchase purchase;
    @ManyToOne
    private ProjectProductItemType projectProductItemType;
    private int optionPrice;
    private int optionCount;

    private LocalDateTime expiredAt;
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Transient
    public String getValidationDateSpans() {
        return String.format("~ %s", TimeUtil.convertLocalDateTimeToStrWithTime(expiredAt));
    }

    @Transient
    public boolean isExpired() {
        return (expiredAt != null) && expiredAt.isBefore(LocalDateTime.now());
    }
}
