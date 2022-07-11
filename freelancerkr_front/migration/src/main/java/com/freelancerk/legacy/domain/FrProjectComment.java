package com.freelancerk.legacy.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
@Entity
public class FrProjectComment {
    @Id
    private Integer fcNo;
    private Integer fqNo;
    private Integer fcNoTop;
    private String fcSubType;
    private String fcType;
    private String cmId;
    private String fmId;
    private String fmIdList;
    private String fcNotice;
    private String fcText;
    private LocalDateTime fcRegDate;
}
