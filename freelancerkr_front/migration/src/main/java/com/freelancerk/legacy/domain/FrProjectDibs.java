package com.freelancerk.legacy.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
@Entity
public class FrProjectDibs {

    @Id
    private Integer fdNo;
    private Integer fpNo;
    private String fmId;
    private LocalDateTime fdRegDate;
}
