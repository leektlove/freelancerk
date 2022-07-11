package com.freelancerk.domain;

import lombok.Data;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class Reference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    @Column(columnDefinition = "TEXT")
    private String content;
    private Long hits;
    @Enumerated(EnumType.STRING)
    private Type type;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum Type {
        FREELANCER("프리랜서"), CLIENT("클라이언트");

        @Getter
        private String desc;

        Type(String desc) {
            this.desc = desc;
        }
    }
}
