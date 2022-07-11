package com.freelancerk.legacy.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
@Entity
public class FrProjectEmailSend {
    @Id
    private String sendNo;
    private LocalDateTime sendDate;
    private String subject;
    private String fmEmail;
    private String fmName;
    private String projectName;
    private Integer keywordCnt;
    private String matchTagArr;
    private String receiveType;
    private String receiveEmail;
}
