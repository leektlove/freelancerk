package com.freelancerk.legacy.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
@Entity
public class FrNoticeList {
    @Id
    private Integer flNo;
    private String frOrder;
    private String frDisplay;
    private String flSubject;
    private String flContent;
    @Column(name = "fl_file_1")
    private String flFile1;
    @Column(name = "fl_file_name_1")
    private String flFileName1;
    private LocalDateTime flRegDate;
    private Long flHit;
}
