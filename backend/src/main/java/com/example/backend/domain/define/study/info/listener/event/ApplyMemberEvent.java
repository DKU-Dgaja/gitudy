package com.example.backend.domain.define.study.info.listener.event;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplyMemberEvent {

    private Long studyLeaderId;

    private String title;

    private String message;
}
