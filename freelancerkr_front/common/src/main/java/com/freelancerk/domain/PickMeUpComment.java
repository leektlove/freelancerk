package com.freelancerk.domain;

import lombok.Data;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class PickMeUpComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private PickMeUp pickMeUp;
    @Column(columnDefinition = "TEXT")
    private String content;
    @ManyToOne
    private User user;
    @Enumerated(EnumType.STRING)
    private UserRole userRole;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum UserRole {
        CLIENT("클라이언트", "#256ad3"), FREELANCER("프리랜서", "#e32c37"), ONESELF("작성자 본인", "black");

        @Getter
        private String desc;
        @Getter
        private String color;

        UserRole(String desc, String color) {
            this.desc = desc;
            this.color = color;
        }
    }
}
