package com.example.backend.domain.define.study.commit.event;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommitApproveEvent {

    private boolean isPushAlarmYn;

    private Long userId;

    private Long studyInfoId;

    private String studyTopic;

    private String studyTodoTopic;
}
