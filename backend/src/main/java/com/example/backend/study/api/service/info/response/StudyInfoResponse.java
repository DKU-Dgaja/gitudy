package com.example.backend.study.api.service.info.response;

import com.example.backend.domain.define.study.info.constant.RepositoryInfo;
import com.example.backend.domain.define.study.info.constant.StudyStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@ToString
public class StudyInfoResponse {
    private Long id;                                // 아이디
    private String topic;                           // 스터디 이름
    private int score;                              // 스터디 활동점수
    private LocalDate endDate;                      // 스터디 종료일
    private String info;                            // 스터디 소개
    private StudyStatus status;                     // 스터디 상태
    private int maximumMember;                      // 스터디 제한 인원
    private int currentMember;                      // 스터디 현재 인원
    private LocalDate lastCommitDay;                // 스터디 마지막 활동 시간
    private String profileImageUrl;                 // 스터디 프로필 사진
    private String notice;                          // 스터디 공지
    private RepositoryInfo repositoryInfo;          // 연동할 깃허브 레포지토리 정보

    @Builder
    public StudyInfoResponse(Long id, String topic, int score, LocalDate endDate, String info, StudyStatus status, int maximumMember, int currentMember, LocalDate lastCommitDay, String profileImageUrl, String notice, RepositoryInfo repositoryInfo) {
        this.id = id;
        this.topic = topic;
        this.score = score;
        this.endDate = endDate;
        this.info = info;
        this.status = status;
        this.maximumMember = maximumMember;
        this.currentMember = currentMember;
        this.lastCommitDay = lastCommitDay;
        this.profileImageUrl = profileImageUrl;
        this.notice = notice;
        this.repositoryInfo = repositoryInfo;
    }
}
