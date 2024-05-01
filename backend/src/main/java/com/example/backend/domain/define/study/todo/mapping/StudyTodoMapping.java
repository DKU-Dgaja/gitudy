package com.example.backend.domain.define.study.todo.mapping;

import com.example.backend.domain.define.BaseEntity;
import com.example.backend.domain.define.study.todo.mapping.constant.StudyTodoStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

@Getter
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "STUDY_TODO_MAPPING")
public class StudyTodoMapping extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "STUDY_TODO_MAPPING_ID")
    private Long id;                        // 아이디

    @Column(name = "TODO_ID", nullable = false)
    private Long todoId;                    // To do ID

    @Column(name = "USER_ID", nullable = false)
    private Long userId;                    // 사용자 ID

    @Enumerated(EnumType.STRING)
    @Column(name = "STUDY_TODO_STATUS")
    @ColumnDefault(value = "'TODO_INCOMPLETE'")
    private StudyTodoStatus status;         // To do 진행상황

    @Builder
    public StudyTodoMapping(Long todoId, Long userId, StudyTodoStatus status) {
        this.todoId = todoId;
        this.userId = userId;
        this.status = status;
    }

    public void updateTodoMappingStatus(StudyTodoStatus status) {
        this.status = status;
    }
}
