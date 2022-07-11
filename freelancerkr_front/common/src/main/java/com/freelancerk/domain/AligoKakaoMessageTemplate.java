package com.freelancerk.domain;

import lombok.Data;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class AligoKakaoMessageTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    @Column(columnDefinition = "TEXT")
    private String message;
    @Enumerated(EnumType.STRING)
    private Code code;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum Code {
        TA_3172("클라이언트 회원가입"), TA_3173("프리랜서 회원가입"), TA_3174("프로젝트 포스팅 완료"),
        TA_3175("컨테스트 포스팅 완료"), TA_3176("섹터 중복 컨테스트 알림", "TA_3176-TA_3176"),
        TA_3177("프로젝트 지원자 알림", null), TA_3178("컨테스트 지원자 알림", null), TA_3180("프로젝트 낙찰 성공(프리랜서)", null),
        TA_3181("컨테스트 당선(프리랜서)", null), TA_3182("컨테스트 포스트 취소 및 환불 안내(클라이언트)", null),
        TA_3183("컨테스트 포스트 취소 및 환불 안내(프리랜서)", null), TA_3184("프로젝트 포스트 마감 예정 안내", null),
        TA_3185("2일 후 프로젝트 포스트 마감 안내"), TA_3186("컨테스트 포스트 마감 및 환불 안내"),
        TA_3893("컨테스트 포스트 마감 예정 안내"),
        TA_3187("2일 후 컨테스트 포스트 자동 마감 안내"), TA_3188("컨테스트 포스트 마감 및 환불 안내"),
        TA_3189("컨테스트 포스트 마감 및 배당 안내"), TA_3190("지급청구"), TA_3191("수정금액 지급청구"), TA_3192("지급청구 거절"),
        TA_3193("지급청구 승인"), TA_3194("완료 및 평가"), TA_3195("2일 후 자동 완료 및 평가"), TA_3196("평가 완료"),
        TA_3197("비밀번호 변경 시도"), TA_3198("참여 제안", "TA_3198-TA_3198"), TA_3199("직거래 검토", "TA_3199-TA_3199"),
        TA_3200("포트폴리오 피드백", "TA_3200-TA_3200"), TA_3201("전체메시지 도착", "TA_3201-TA_3201"),
        TA_3202("메시지 도착", "TA_3202-TA_3202"), TA_3203("메시지 도착", "TA_3203-TA_3203"),
        TA_3204("메시지 도착", "TA_3204-TA_3204"), TA_3205("메시지 도착", "TA_3205-TA_3205"),
        TA_3206("포트폴리오 공개 종료"), TA_3207("2일 후 포트폴리오 공개 종료 예정"), TA_3208("2일 후 픽미업 옵션 종료 예정"), TA_9935("인증코드 안내"),
        TB_0735("문의사항 답변완료"), TB_1311("지급완료(클라이언트)"), TB_1312("지급완료(프리랜서)"), TB_1398("컨테스트 종료 안내"),
        TB_3000("지급청구_2"), TB_3001("수정금액 지급청구_2");

        @Getter
        private String desc;
        @Getter
        private String realCode;

        Code(String desc) {
            this.desc = desc;
        }

        Code(String desc, String realCode) {
            this.desc = desc;
            this.realCode = realCode;
        }
    }
}
