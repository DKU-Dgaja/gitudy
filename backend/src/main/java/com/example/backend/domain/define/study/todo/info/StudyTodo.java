package com.example.backend.domain.define.study.todo.info;

import com.example.backend.domain.define.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "STUDY_TODO")
public class StudyTodo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "STUDY_TODO_ID")
    private Long id;                            // 아이디

    @Column(name = "STUDY_INFO_ID", nullable = false)
    private Long studyInfoId;                   // 스터디 ID

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
    public StudyTodo(Long id, Long studyInfoId, String title, String detail, String todoLink, LocalDate todoDate) {
        this.id = id;
        this.studyInfoId = studyInfoId;
        this.title = title;
        this.detail = detail;
        this.todoLink = todoLink;
        this.todoDate = todoDate;
    }


    public void updateStudyTodo(String title, String detail, String todoLink, LocalDate todoDate) {
        this.title = title;
        this.detail = detail;
        this.todoLink = todoLink;
        this.todoDate = todoDate;
    }
}
