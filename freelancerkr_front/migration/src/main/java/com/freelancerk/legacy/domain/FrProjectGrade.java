package com.freelancerk.legacy.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
@Entity
public class FrProjectGrade {
    @Id
    private Integer fgNo;
    private Integer fpNo;
    private String cmId;
    private String fmId;
    private String fgType;
    @Column(name = "fg_grade_1")
    private Integer fgGrade1;
    @Column(name = "fg_grade_2")
    private Integer fgGrade2;
    @Column(name = "fg_grade_3")
    private Integer fgGrade3;
    @Column(name = "fg_grade_4")
    private Integer fgGrade4;
    @Column(name = "fg_grade_5")
    private Integer fgGrade5;
    private String fgText;
    private LocalDateTime fgRegDate;
    private LocalDateTime fgModDate;
}
