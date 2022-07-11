package com.freelancerk.domain;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Data
public class DanalCertificationResult {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    private String impUid;
    private String internalUid;
    private String merchantId;
    private String name;
    private String gender;
    private Long birth;
    private boolean signed;
    private String uniqueKey;
    private String phone;
    private String carrier;
    private LocalDateTime createdAt;
}
