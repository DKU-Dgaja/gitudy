package com.example.backend.study.api.controller.bookmark.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IsMyBookmarkResponse {
    private boolean isMyBookmark;
}
