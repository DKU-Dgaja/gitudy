package com.example.backend.domain.define.study.member.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawalMemberEvent {
    private Long studyLeaderId;
    private String withdrawalMemberName;
    private String studyInfoTopic;
}