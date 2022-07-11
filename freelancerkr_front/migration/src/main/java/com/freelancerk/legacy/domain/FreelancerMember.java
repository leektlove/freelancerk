package com.freelancerk.legacy.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
@Entity
public class FreelancerMember {
    @Id
    private int fmNo;
    private String fmId;
    private String fmPassword;
    private int fmPasswordFail;
    private LocalDateTime fmPasswordFailDate;
    private String fmRegType;
    private LocalDateTime fmRegDate;
    private LocalDateTime fmModDate;
    private String fmLeaveDate;
    private String fmLeaveType;
    private String fmLeaveText;
    private String fmLostCertify;
    private String fmName;
    private String fmNick;
    private String fmNameType;
    private String fmResidentRegistration;
    private String fmRealName;
    private String fmEmail;
    private String fmEmailNotice;
    private String fmEmailCertify;
    private LocalDateTime fmEmailCertifyDate;
    private String fmHp;
    private String fmHpYn;
    private String fmCompany;
    private String fmBusinessType;
    private String fmLicenseNumber;
    private String fmLicenseNumberYn;
    private String fmBankName;
    private String fmBankNumber;
    private String fmAccountName;
    private String fmProfileFile;
    private String fmProfileSampleYn;
    private String fmProfileSampleFile;
    private String fmLicenseFile;
    private String fmLicenseFileName;
    private String fmEscrowYn;
    private String fmServicePrice;
    private String fcId;
    @Column(name = "fc_id_2")
    private String fcId2;
    private String fmTag;
    private String fmIntroduce;
    private String fmIntroduceCareer;
    private String fmIntroduceFile;
    private String fmIntroduceFileName;
    @Column(name = "fm_hope_1")
    private String fmHope1;
    @Column(name = "fm_hope_2")
    private String fmHoep2;
    @Column(name = "fm_hope_3")
    private String fmHope3;
    @Column(name = "fm_hope_4")
    private String fmHope4;
    @Column(name = "fm_sns_1")
    private String fmSns1;
    @Column(name = "fm_sns_2")
    private String fmSns2;
    @Column(name = "fm_sns_3")
    private String fmSns3;
    @Column(name = "fm_sns_4")
    private String fmSns4;
    @Column(name = "fm_text_1")
    private String fmText1;
    @Column(name = "fm_text_2")
    private String fmText2;
    @Column(name = "fm_text_3")
    private String fmText3;
    @Column(name = "fm_display_1_1")
    private String fmDisplay11;
    @Column(name = "fm_display_1_2")
    private String fmDisplay12;
    @Column(name = "fm_display_1_3")
    private String fmDisplay13;
    @Column(name = "fm_display_2_1")
    private String fmDisplay21;
    @Column(name = "fm_display_2_2")
    private String fmDisplay22;
    @Column(name = "fm_display_2_3")
    private String fmDisplay23;
    @Column(name = "fm_display_3_1")
    private String fmDisplay31;
    @Column(name = "fm_display_3_2")
    private String fmDisplay32;
    @Column(name = "fm_display_3_3")
    private String fmDisplay33;
    private String fmMemo;
    private int fmPortfolioCnt;
    private LocalDateTime fmLoginDate;
}
