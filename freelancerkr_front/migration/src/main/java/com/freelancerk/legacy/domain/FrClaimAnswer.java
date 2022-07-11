package com.freelancerk.legacy.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
@Entity
public class FrClaimAnswer {

    @Id
    private Integer faNo;
    private Integer fcNo;
    private String faContent;
    private LocalDateTime faRegDate;

}
