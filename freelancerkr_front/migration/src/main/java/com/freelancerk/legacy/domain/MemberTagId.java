package com.freelancerk.legacy.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class MemberTagId implements Serializable {

    @Column(insertable = false, updatable = false)
    private String fmId;
    @Column(insertable = false, updatable = false)
    private String fcId;
    @Column(name = "fc_id_2", insertable = false, updatable = false)
    private String fcId2;
}
