package com.freelancerk.legacy.domain;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@IdClass(FrTag.class)
public class FrTag implements Serializable {
    @Id
    private String fcId;
    @Id
    private String ftName;
    private Integer ftOrder;
    private Integer ftCnt;
}