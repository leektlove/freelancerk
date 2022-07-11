package com.freelancerk.legacy.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
public class FrProjectTag {
    @Id
    private Integer fpNo;
    private String fcId;
    @Column(name = "fc_id_2")
    private String fcId2;
    private Integer ftOrder;
    private String ftTag;
}
