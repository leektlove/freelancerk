package com.freelancerk.legacy.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
@Entity
public class FrProjectOffer {
    @Id
    private Integer foNo;
    private Integer fpNo;
    private String fmId;
    private String foOfferYn;
    private String foCanType;
    private String foCanText;
    private LocalDateTime foRegDate;
    private LocalDateTime foOffDate;
    private LocalDateTime foCanDate;
}
