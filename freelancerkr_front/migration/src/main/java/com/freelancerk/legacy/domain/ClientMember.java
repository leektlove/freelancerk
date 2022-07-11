package com.freelancerk.legacy.domain;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class ClientMember {

    @Id
    private Long cmNo;
    private String cmId;
    private String cmHp;
    private String cmHpYn;
    private String cmCompany;
    private String cmLicenseNumber;
    private String cmLicenseNumberYn;
    private String cmBankName;
    private String cmBankNumber;
    private String cmAccountName;
    private String cmProfileFile;
    private String cmProfileSampleYn;
    private String cmProfileSampleFile;
    private String cmLicenseFile;
    private String cmLicenseFileName;
    private String cmEscrowYn;
    private Integer cmEscrowPrice;
    private String cmBusinessType;
    @Column(name = "cm_sns_1")
    private String cmSns1;
    @Column(name = "cm_sns_2")
    private String cmSns2;
    @Column(name = "cm_sns_3")
    private String cmSns3;
    @Column(name = "cm_sns_4")
    private String cmSns4;
    @Column(name = "cm_display_1_1")
    private String cmDisplay11;
    @Column(name = "cm_display_1_2")
    private String cmDisplay12;
    @Column(name = "cm_display_1_3")
    private String cmDisplay13;
    private String cmMemo;
    private LocalDateTime cmLoginDate;
    private String cmPassword;
    private Integer cmPasswordFail;
    private LocalDateTime cmPasswordFailDate;
    private String cmRegType;
    private LocalDateTime cmRegDate;
    private LocalDateTime cmModDate;
    private String cmLeaveDate;
    private String cmLeaveType;
    private String cmLeaveText;
    private String cmLostCertify;
    private String cmName;
    private String cmNick;
    private String cmNameType;
    private String cmEmail;
    private String cmEmailNotice;
    private String cmEmailCertify;
    private LocalDateTime cmEmailCertifyDate;
}
