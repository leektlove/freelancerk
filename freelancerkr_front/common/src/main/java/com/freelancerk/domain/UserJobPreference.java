package com.freelancerk.domain;

import lombok.Data;
import lombok.Getter;

import javax.persistence.*;

@Data
@Entity
public class UserJobPreference {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne
	private User user;
    @Enumerated(EnumType.STRING)
	private Project.ExpectedPeriod projectDuration;
    @Enumerated(EnumType.STRING)
    private ProjectSize projectSize;
    @Enumerated(EnumType.STRING)
    private Project.PayCriteria payCriteria;
    @Enumerated(EnumType.STRING)
    private Project.WorkPlace workPlace;

	public enum ProjectSize {
		ANY("무관"), SMALL("소형 프로젝트(300만원 미만)"), MIDDLE("중형 프로젝트(1,000만원 미만)"),
		MIDDLE_BIG("중대형 프로젝트(5,000만원 미만)"), BIG("대형 프로젝트(1억원 미만)"), SUPER_BIG("초대형 프로젝트(1억원 이상)");

		@Getter
		private String name;

		ProjectSize(String name) {
			this.name = name;
		}
	}
}
