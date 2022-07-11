package com.freelancerk.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.freelancerk.FileUtil;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class PickMeUpDetailFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fileUrl;
    private String videoImageUrl;
    private long fileSize;
    private String fileOriginName;
    @JsonManagedReference
    @ManyToOne
    private PickMeUp pickMeUp;
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private ContestEntryFile.Type fileType;

    @Transient
    public String getHumanReadableSize() {
        return FileUtil.humanReadableByteCount(fileSize, true);
    }

    public enum Type {
        IMAGE, VIDEO, AUDIO, ETC
    }
}
