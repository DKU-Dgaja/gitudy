package com.example.backend.study.api.controller.bookmark;

import com.example.backend.auth.api.service.auth.AuthService;
import com.example.backend.common.response.JsonResult;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.study.api.controller.bookmark.response.BookmarkInfoListAndCursorIdxResponse;
import com.example.backend.study.api.service.bookmark.StudyBookmarkService;
import com.example.backend.study.api.service.bookmark.response.BookmarkInfoResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/bookmarks")
public class StudyBookmarkController {
    private final AuthService authService;
    private final StudyBookmarkService studyBookmarkService;

    @ApiResponse(responseCode = "200",
            description = "북마크 조회 성공",
            content = @Content(schema = @Schema(implementation = BookmarkInfoListAndCursorIdxResponse.class)))
    @GetMapping("/user/{userId}")
    public JsonResult<?> userBookmarkList(@AuthenticationPrincipal User user,
                                          @PathVariable(name = "userId") Long userId,
                                          @Min(value = 0, message = "Cursor index cannot be negative") @RequestParam(name = "cursorIdx") Long cursorIdx,
                                          @Min(value = 1, message = "Limit cannot be less than 1") @RequestParam(name = "limit", defaultValue = "5") Long limit) {

        authService.authenticate(userId, user);
        List<BookmarkInfoResponse> bookmarkInfoList = studyBookmarkService.selectUserBookmarkList(userId, cursorIdx, limit);

        // 다음 cursorIdx
        Long nextCursorIdx = 0L;
        if (!bookmarkInfoList.isEmpty()) {
            nextCursorIdx = bookmarkInfoList.get(bookmarkInfoList.size() - 1).getId();
        }

        return JsonResult.successOf(BookmarkInfoListAndCursorIdxResponse.builder()
                .bookmarkInfoList(bookmarkInfoList)
                .cursorIdx(nextCursorIdx)
                .build());
    }


}
