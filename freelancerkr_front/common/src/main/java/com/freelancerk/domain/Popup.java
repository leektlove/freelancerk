package com.freelancerk.domain;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
public class Popup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private AdminUser adminUser;
    private LocalDateTime createdAt;
    private String imageUrl;
    private String linkUrl;
    private String memo;
    private boolean hidden;
    @Enumerated(EnumType.STRING)
    private DeviceType deviceType;
    @Enumerated(EnumType.STRING)
    private LinkType linkType;
    private Integer positionTop;
    private Integer positionLeft;
    private Integer positionWidth;
    private Integer positionHeight;
    private LocalDate startAt;
    private LocalDate endAt;

    public enum DeviceType {
        PC, MOBILE
    }

    public enum LinkType {
        CURRENT, NEW
    }
}
