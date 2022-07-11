package com.freelancerk.legacy.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
@Entity
public class ClientMemberPrice {
    @Id
    private Long cpNo;
    private String cmId;
    private String fpNo;
    private String cpType;
    private Integer cpTotalPrice;
    private Integer cpPrice;
    private LocalDateTime cpApplicationDay;
    private LocalDateTime cpRegDate;
    private LocalDateTime cpModDate;
    private LocalDateTime cpTextDate;
    private LocalDateTime cpDay;
    private LocalDateTime cpCancelDay;
    private String cpMemo;
    private String cpCancelMemo;
    @Column(name = "cp_yn_1")
    private String cpYn1;
    @Column(name = "cp_yn_2")
    private String cpYn2;
    private String cmBusinessType;
}
