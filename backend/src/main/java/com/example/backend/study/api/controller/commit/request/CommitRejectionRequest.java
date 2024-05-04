package com.example.backend.study.api.controller.commit.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommitRejectionRequest {

    @NotBlank(message = "거절 이유는 공백일 수 없습니다.")
    private String rejectionReason;
}
