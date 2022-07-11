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
public class ProjectItemTicket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JsonBackReference
    @ManyToOne
    private Project project;
    @ManyToOne
    private ProjectProductItemType projectProductItemType;

    private LocalDateTime expiredAt;
    @CreationTimestamp
    private LocalDateTime createdAt;
    private boolean invalid;

    @Transient
    public String getValidationDateSpans() {
        return String.format("~ %s", TimeUtil.convertLocalDateTimeToStrWithTime(expiredAt));
    }
}
