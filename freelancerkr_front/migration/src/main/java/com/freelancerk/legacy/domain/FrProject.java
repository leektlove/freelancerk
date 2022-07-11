package com.freelancerk.legacy.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
@Entity
public class FrProject {
    @Id
    private Integer fpNo;
    private String fpSubject;
    private String fpIntroduce;
    private String cmId;
    private String fmId;
    private String fmBusinessType;
    private String fcId;
    @Column(name = "fc_id_2")
    private String fcId2;
    private String fpTag;
    private String fpType;
    private String fpCmIngYn;
    private String fpFmIngYn;
    private String fpCmComYn;
    private String fpFmComYn;
    private String fpIntroduceFile;
    private String fpIntroduceFileName;
    @Column(name = "fp_hope_1")
    private String fpHope1;
    @Column(name = "fp_hope_2")
    private String fpHope2;
    @Column(name = "fp_hope_3")
    private String fpHope3;
    @Column(name = "fp_hope_4")
    private String fpHope4;
    @Column(name = "fp_notice_1")
    private String fpNotice1;
    @Column(name = "fp_notice_2")
    private String fpNotice2;
    private String fpEscrowYn;
    private String fpTerm;
    private String fpTermEtc;
    private LocalDateTime fpRegDate;
    private LocalDateTime fpModDate;
    private LocalDateTime fpDisDate;
    private LocalDateTime fpEndDate;
    private Integer fpEndDateCnt;
    private LocalDateTime fpCanDate;
    private String fpCanType;
    private String fpCanText;
    private String fpConDay;
    private String fpComDay;
    private Integer fpContractPrice;
    private Integer fpTaxPrice;
    private Integer fpTotalPrice;
    private LocalDateTime fpWinDate;
    private LocalDateTime fpTemDate;
    private LocalDateTime fpConDate;
    private LocalDateTime fpComDate;
    private String fpMemo;
}
