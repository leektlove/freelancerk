package com.freelancerk.domain;

import lombok.Data;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
public class Apply {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String uid;
    private String work;
    private String name;
    private String email;
    private String cellphone;
    private String pass1;
    private String pass2;
    private String pass3;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @Transient
    private String status;
    @Transient
    private String errorMsg;
    @Transient
    private List<Apply> list;
    @Transient
    private String dateTime;
    
}
