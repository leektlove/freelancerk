package com.freelancerk.domain;

import lombok.Data;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
public class Inquiry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private User user;
    private String title;
    @Column(columnDefinition = "TEXT")
    private String content;
    private String contact;
    @Enumerated(EnumType.STRING)
    private Status status;
    private LocalDateTime createdAt;
    @Enumerated(EnumType.STRING)
    private User.Role userRole;
    private boolean answerAlarm;

    @Transient
    private List<InquiryAnswer> answers;

    @Transient
    private int numberOfInquiries;

    public enum Status {
        REGISTERED("접수"), IN_PROGRESS("상담 중"), COMPLETED("상담완료");

        @Getter
        private String desc;

        Status(String desc) {
            this.desc = desc;
        }
    }
}
