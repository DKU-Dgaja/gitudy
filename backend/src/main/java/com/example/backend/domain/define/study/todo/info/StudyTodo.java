package com.example.backend.domain.define.study.todo.info;

import com.example.backend.domain.define.BaseEntity;
import com.example.backend.domain.define.study.info.StudyInfo;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "STUDY_TODO")
public class StudyTodo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "STUDY_TODO_ID")
    private Long id;                            // 아이디

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STUDY_INFO_ID", nullable = false)
    private StudyInfo studyInfo;                // 스터디 정보

    @Column(name = "TITLE", nullable = false)
    private String title;                       // To do 이름

    @Column(name = "DETAIL")
    private String detail;                      // To do 설명

    @Temporal(TemporalType.DATE)
    @Column(name = "TODO_DATE", nullable = false)
    private LocalDate todoDate;                     // To do 날짜

    @Builder
    public StudyTodo(StudyInfo studyInfo, String title, String detail, LocalDate todoDate) {
        this.studyInfo = studyInfo;
        this.title = title;
        this.detail = detail;
        this.todoDate = todoDate;
    }
}
