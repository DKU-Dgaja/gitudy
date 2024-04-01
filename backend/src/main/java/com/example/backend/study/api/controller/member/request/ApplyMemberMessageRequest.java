package com.example.backend.study.api.controller.member.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplyMemberMessageRequest {

    private String message;          // 팀장에게 한마디
}
