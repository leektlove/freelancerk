package com.freelancerk.legacy.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
@Entity
public class FrProjectApply {
    @Id
    private Integer faNo;
    private Integer fpNo;
    private Integer faProjectNotice;
    private String faType;
    private String fmId;
    private String fmBusinessType;
    private Integer faPrice;
    private LocalDateTime faRegDate;
}
