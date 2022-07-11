package com.freelancerk.legacy.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
public class FrProjectFile {
    @Id
    private Integer fpNo;
    private String ffType;
    @Column(name = "ff_file_1")
    private String ffFile1;
    @Column(name = "ff_file_1_name")
    private String ffFile1Name;
    @Column(name = "ff_file_2")
    private String ffFile2;
    @Column(name = "ff_file_2_name")
    private String ffFile2Name;
    @Column(name = "ff_file_3")
    private String ffFile3;
    @Column(name = "ff_file_3_name")
    private String ffFile3Name;
    @Column(name = "ff_file_4")
    private String ffFile4;
    @Column(name = "ff_file_4_name")
    private String ffFile4Name;
    @Column(name = "ff_file_5")
    private String ffFile5;
    @Column(name = "ff_file_5_name")
    private String ffFile5Name;
}
