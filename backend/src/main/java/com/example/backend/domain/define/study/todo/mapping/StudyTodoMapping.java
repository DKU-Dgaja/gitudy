package com.example.backend.domain.define.study.todo.mapping;

import com.example.backend.domain.define.BaseEntity;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.study.todo.info.StudyTodo;
import com.example.backend.domain.define.study.todo.mapping.constant.StudyTodoStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "STUDY_TODO_MAPPING")
public class StudyTodoMapping extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "STUDY_TODO_MAPPING_ID")
    private Long id;                        // 아이디

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STUDY_TODO_ID", nullable = false)
    private StudyTodo todo;                 // To do 정보

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;                      // To do 할당자

    @Enumerated(EnumType.STRING)
    @Column(name = "STUDY_TODO_STATUS")
    @ColumnDefault(value = "'TODO_INCOMPLETE'")
    private StudyTodoStatus status;         // To do 진행상황

    @Builder
    public StudyTodoMapping(StudyTodo todo, User user, StudyTodoStatus status) {
        this.todo = todo;
        this.user = user;
        this.status = status;
    }
}
