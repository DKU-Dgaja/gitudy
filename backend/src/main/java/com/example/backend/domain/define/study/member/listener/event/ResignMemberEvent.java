package com.example.backend.domain.define.study.member.listener.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResignMemberEvent {
    private Long resignMemberId;
    private String studyInfoTopic;
}
