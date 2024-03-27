package com.example.backend.domain.define.study.todo.info;

import com.example.backend.domain.define.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.Random;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "STUDY_TODO")
public class StudyTodo extends BaseEntity {

    private static final String ALLOWED_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private final int TODO_CODE_LENGTH = 6;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "STUDY_TODO_ID")
    private Long id;                            // 아이디

    @Column(name = "STUDY_INFO_ID", nullable = false)
    private Long studyInfoId;                   // 스터디 ID

    @Column(name = "TODO_CODE", nullable = false, length = TODO_CODE_LENGTH)
    private String todoCode;                    // To do를 식별할 6자리 코드

    @Column(name = "TITLE", nullable = false)
    private String title;                       // To do 이름

    @Column(name = "DETAIL")
    private String detail;                      // To do 설명

    @Column(name = "TODO_LINK")
    private String todoLink;                    // To do 링크

    @Temporal(TemporalType.DATE)
    @Column(name = "TODO_DATE", nullable = false)
    private LocalDate todoDate;                 // To do 날짜

    @Builder
    public StudyTodo(Long studyInfoId, String title, String detail, String todoLink, LocalDate todoDate) {
        this.studyInfoId = studyInfoId;
        this.todoCode = generateRandomString();
        this.title = title;
        this.detail = detail;
        this.todoLink = todoLink;
        this.todoDate = todoDate;
    }

    private String generateRandomString() {
        Random random = new SecureRandom();
        StringBuilder sb = new StringBuilder(TODO_CODE_LENGTH);
        for (int i = 0; i < TODO_CODE_LENGTH; i++) {
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        }
        return sb.toString();
    }

    public void updateStudyTodo(String title, String detail, String todoLink, LocalDate todoDate) {
        this.title = title;
        this.detail = detail;
        this.todoLink = todoLink;
        this.todoDate = todoDate;
    }

    // 테스트를 위해 코드 업데이트 메서드 추가
    public void updateTodoCode(String code) {
        this.todoCode = code;
    }
}
