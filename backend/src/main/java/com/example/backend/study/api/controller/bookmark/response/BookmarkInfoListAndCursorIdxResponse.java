package com.example.backend.study.api.controller.bookmark.response;

import com.example.backend.study.api.service.bookmark.response.BookmarkInfoResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class BookmarkInfoListAndCursorIdxResponse {
    private List<BookmarkInfoResponse> bookmarkInfoList;
    private Long cursorIdx;

    @Builder
    public BookmarkInfoListAndCursorIdxResponse(List<BookmarkInfoResponse> bookmarkInfoList, Long cursorIdx) {
        this.bookmarkInfoList = bookmarkInfoList;
        this.cursorIdx = cursorIdx;
    }
}
