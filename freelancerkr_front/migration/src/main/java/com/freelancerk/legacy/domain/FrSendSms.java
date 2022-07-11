package com.freelancerk.legacy.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
@Entity
public class FrSendSms {
    @Id
    private Integer fsNo;
    private String fsType;
    private String fsSubject;
    private String fsContent;
    private Integer fsSendCnt;
    private String fsSendInfo;
    private LocalDateTime fsRegDate;
}
