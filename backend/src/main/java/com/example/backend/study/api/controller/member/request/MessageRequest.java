package com.example.backend.study.api.controller.member.request;


import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageRequest {

    @Size(max = 100, message = "메세지 100자 이내")
    private String message;          // 팀장에게 한마디 or 멤버에게 보낼 메세지 or 탈퇴 메세지
}
