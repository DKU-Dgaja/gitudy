package com.example.backend.study.api.controller.member.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageRequest {

    private String message;          // 팀장에게 한마디 or 멤버에게 보낼 메세지
}
