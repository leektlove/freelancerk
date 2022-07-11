package com.freelancerk.legacy.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
public class FrCategory {
    @Id
    private String fcId;
    private Integer frOrder;
    private String frDisplay;
    private String fcSubject;
    private String fcMenuImg;
    private String fcRegisterImg;
    private String fcImg;
    private String fcTag;
}
