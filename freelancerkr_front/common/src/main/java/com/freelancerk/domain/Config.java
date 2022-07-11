package com.freelancerk.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity
public class Config {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String siteUrl;
    private String adminName;
    private String adminEmail;
    private String companyName;
    private String companyAddr;
    private String tel;
    private String fax;
    private String owner;
    private String companyNo;
    private String personal;
    private String communication;
    private String upjong;
    private String uptae;
    private String bankName;
    private String bankAccountNumber;
    private String stipulation;
    private String privacy;
    private String information;
    private String noticeNumber;
    private String smsHp;
    private String smsId;
    private String smsKey;
    private String smsServerIp;
    private String smsServerPort;
    private String facebookId;
    private String facebookKey;
    private String naverId;
    private String naverKey;
    private String twitterId;
    private String twitterKEy;
    private String instagramId;
    private String instagramKey;
    private String googleId;
    private String googleKey;
    private String kakaoId;
    private String kakaoKey;
    private String wechatId;
    private String wechatKey;
    private String youtubeId;
    private String youtubeKey;
}
