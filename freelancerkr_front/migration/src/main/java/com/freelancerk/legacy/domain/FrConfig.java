package com.freelancerk.legacy.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
public class FrConfig {
    @Id
    private String fcTitle;
    private String fcSite;
    private String fcAdminName;
    private String fcAdminEmail;
    private String fcCompanyName;
    private String fcCompanyAddr;
    private String fcTel;
    private String fcFax;
    private String fcOwner;
    private String fcCompanyNo;
    private String fcPersonal;
    private String fcCommunication;
    private String fcUpjong;
    private String fcUptae;
    private String fcBankName;
    private String fcBankNumber;
    private String fcAccountName;
    private String fcStipulationName;
    private String fcPrivacy;
    private String fcInformation;
    private String fcNoticeNumber;
    private String fcSmsHp;
    private String fcSmsId;
    private String fcSmsKey;
    private String fcSmsServerIp;
    private String fcSmsServerPort;
    private String fcFacebookId;
    private String fcFacebookKey;
    private String fcNaverId;
    private String fcNaverKey;
    private String fcTwitterId;
    private String fcTwitterKey;
    private String fcInstagramId;
    private String fcInstagramKey;
    private String fcGoogleId;
    private String fcGoogleKey;
    private String fcKakaoId;
    private String fcKakaoKey;
    private String fcWechatId;
    private String fcWechatKey;
    private String fcYoutubeId;
    private String fcYoutebeKey;
}
