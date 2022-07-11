package com.freelancerk.domain;

import lombok.Data;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class ProjectProposition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Project project;
    @ManyToOne
    private PickMeUp pickMeUp;
    @ManyToOne
    private User freelancer;
    private String contractUrl;
    private LocalDateTime acceptAt;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    @Enumerated(EnumType.STRING)
    private Status status;
    
    @Column(columnDefinition = "TEXT")
    private String rejectReason;

    @Transient
    private ProjectBid myBid;

    @Transient
    public boolean isExistsBid() {
        return myBid != null || Status.ACCEPT.equals(status);
    }


    public enum Status {
        PROPOSE("프리랜서 검토 중"), ACCEPT("프리랜서 지원 완료!"), REJECT("프리랜서가 거절하였습니다");

        @Getter
        private String desc;

        Status(String desc) {
            this.desc = desc;
        }
    }
    
}
