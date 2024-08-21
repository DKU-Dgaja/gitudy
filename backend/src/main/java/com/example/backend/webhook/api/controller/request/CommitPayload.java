package com.example.backend.webhook.api.controller.request;

import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record CommitPayload(
        String commitId,             // 커밋 ID
        String message,              // 커밋 메시지
        String username,             // 커밋한 사용자의 깃허브 ID
        List<String> commitAdded,    // 추가된 파일 리스트
        List<String> commitModified, // 수정된 파일 리스트
        LocalDate commitDate         // 커밋 날짜
) {}
