package com.freelancerk.domain;

import com.freelancerk.FileUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;

@EqualsAndHashCode(exclude = {"contestEntry"})
@ToString(exclude = {"contestEntry"})
@Data
@Entity
public class ContestEntryFile {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne
	private User user;
	@ManyToOne
	private ProjectBid contestEntry;
	private String fileTitle;
	private String fileOriginName;
	private String croppedFileUrl;
	private String fileUrl;
	private String videoImageUrl;
	private boolean representative;
	private long fileSize;
	
	@Enumerated(EnumType.STRING)
	private Type fileType;

	@Transient
	public String getHumanReadableSize() {
		return FileUtil.humanReadableByteCount(fileSize, true);
	}

	public enum Type {
		IMAGE, VIDEO, AUDIO, ETC
	}
}
