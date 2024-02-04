package com.example.backend.study.api.controller.commit.request;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommitInfoPageRequest {

    @Min(value = 1, message = "Page size must be greater than 0")
    private int pageSize;

    @Min(value = 0, message = "Cursor index cannot be negative")
    @Nullable
    private Long cursorIdx;
}
