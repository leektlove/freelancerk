package com.freelancerk.domain;

import com.freelancerk.type.NoticeType;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class Notice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    @Column(columnDefinition = "TEXT")
    private String content;
    private long hits;
    private String fileUrl;
    private boolean hidden;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    @Enumerated(EnumType.STRING)
    private NoticeType type;

    private String mainImageUrl;
    private String subtitle;
    private String linkUrl;

    @Transient
    private String editordata;
    @Transient
    private MultipartFile file;

    @Transient
    public boolean isNew() {
        if (createdAt == null) return false;
        return createdAt.isAfter(LocalDateTime.now().minusWeeks(2));
    }

    @Transient
    public String getAbbreviatedContents() {
        if (StringUtils.isEmpty(content)) return null;
        Document document = Jsoup.parse(content);
        return StringUtils.abbreviate(document.body().text(), 200);
    }

    @Transient
    public String getImageUrl() {
        if (StringUtils.isEmpty(content)) return null;
        Document document = Jsoup.parse(content);
        Elements imageElements = document.select("img");
        if (imageElements != null && imageElements.size() > 0) {
           return imageElements.get(0).attr("src");
        }
        return null;
    }
}
