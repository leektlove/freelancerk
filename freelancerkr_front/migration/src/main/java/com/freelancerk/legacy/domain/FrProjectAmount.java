package com.freelancerk.legacy.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
@Entity
public class FrProjectAmount {
    @Id
    private Integer faNo;
    private Integer fpNo;
    private String faType;
    private Integer faPrice;
    @Column(name = "fa_memo_1")
    private String faMemo1;
    @Column(name = "fa_memo_2")
    private String faMemo2;
    @Column(name = "fa_memo_3")
    private String faMemo3;
    @Column(name = "fa_memo_4")
    private String faMemo4;
    @Column(name = "fa_date_1")
    private LocalDateTime faDate1;
    @Column(name = "fa_date_2")
    private LocalDateTime faDate2;
    @Column(name = "fa_date_3")
    private LocalDateTime faDate3;
    @Column(name = "fa_date_4")
    private LocalDateTime faDate4;
}
