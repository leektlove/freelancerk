package com.freelancerk.legacy.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
@Entity
public class FrClaim {
    @Id
    private Integer fcNo;
    private Integer fcCategory;
    private String fcType;
    private String cmId;
    private String fmId;
    private String fcSubject;
    private String fcContent;
    private LocalDateTime fcRegDate;
}
