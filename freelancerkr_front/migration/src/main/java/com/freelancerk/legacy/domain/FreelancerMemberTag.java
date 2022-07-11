package com.freelancerk.legacy.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Data
@Entity
public class FreelancerMemberTag {
    @EmbeddedId MemberTagId memberTagId;
    @Column(insertable = false, updatable = false)
    private String fmId;
    @Column(insertable = false, updatable = false)
    private String fcId;
    @Column(name = "fc_id_2", insertable = false, updatable = false)
    private String fcId2;
    private int ftOrder;
    private String ftTag;

}