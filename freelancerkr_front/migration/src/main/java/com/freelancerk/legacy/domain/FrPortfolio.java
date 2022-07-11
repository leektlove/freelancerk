package com.freelancerk.legacy.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
@Entity
public class FrPortfolio {
    @Id
    private Integer fpNo;
    private String paFpNo;
    private Integer fpOrder;
    private String fpSubject;
    private String fpContent;
    private String fmId;
    private String fcId;
    @Column(name = "fc_id_2")
    private String fcId2;
    private String fpStartDay;
    private String fpEndDay;
    private String fpDisplayYn;
    private String fpAdminDisplayYn;
    @Column(name = "fp_img_1")
    private String fpImg1;
    @Column(name = "fp_img_2")
    private String fpImg2;
    @Column(name = "fp_img_3")
    private String fpImg3;
    @Column(name = "fp_img_4")
    private String fpImg4;
    @Column(name = "fp_img_5")
    private String fpImg5;
    @Column(name = "fp_img_6")
    private String fpImg6;
    private LocalDateTime fpRegDate;
}
