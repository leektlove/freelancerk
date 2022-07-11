package com.freelancerk.legacy.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
public class FrPopup {
    @Id
    private Integer fpNo;
    private String fpDisplay;
    private String fpType;
    private String fpStartDay;
    private String fpEndDay;
    private String fpLinkType;
    private String fpLink;
    private Integer fpTop;
    private Integer fpLeft;
    private Integer fpWidth;
    private Integer fpHeight;
    private String fpImg;
    private String fpMemo;
}
