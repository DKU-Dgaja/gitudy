package com.example.backend.study.api.controller.bookmark;

import com.example.backend.auth.api.controller.auth.response.UserInfoResponse;
import com.example.backend.auth.api.service.auth.AuthService;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.study.api.controller.bookmark.response.BookmarkInfoListAndCursorIdxResponse;
import com.example.backend.study.api.controller.bookmark.response.IsMyBookmarkResponse;
import com.example.backend.study.api.service.bookmark.StudyBookmarkService;
import com.example.backend.study.api.service.bookmark.response.BookmarkInfoResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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

    @ApiResponse(responseCode = "200", description = "북마크 조회 성공", content = @Content(schema = @Schema(implementation = BookmarkInfoListAndCursorIdxResponse.class)))
    @GetMapping("")
    public ResponseEntity<BookmarkInfoListAndCursorIdxResponse> userBookmarkList(@AuthenticationPrincipal User user,
                                                                                 @Min(value = 0, message = "Cursor index cannot be negative") @RequestParam(name = "cursorIdx", required = false) Long cursorIdx,
                                                                                 @Min(value = 1, message = "Limit cannot be less than 1") @RequestParam(name = "limit", defaultValue = "5") Long limit) {

        UserInfoResponse userInfo = authService.findUserInfo(user);
        List<BookmarkInfoResponse> bookmarkInfoList = studyBookmarkService.selectUserBookmarkList(userInfo.getUserId(), cursorIdx, limit);

        BookmarkInfoListAndCursorIdxResponse response = BookmarkInfoListAndCursorIdxResponse.builder()
                .bookmarkInfoList(bookmarkInfoList)
                .build();

        response.getNextCursorIdx();

        return ResponseEntity.ok().body(response);
    }

    @ApiResponse(responseCode = "200", description = "북마크 등록/삭제 성공")
    @GetMapping("study/{studyInfoId}")
    public ResponseEntity<Void> handleBookmark(@AuthenticationPrincipal User user,
                                               @PathVariable(name = "studyInfoId") Long studyInfoId) {

        // 시큐리티 컨텍스트의 user와 일치하는 DB user 정보 조회
        UserInfoResponse userInfo = authService.findUserInfo(user);

        // 북마크 등록 or 삭제
        studyBookmarkService.handleBookmark(userInfo.getUserId(), studyInfoId);

        return ResponseEntity.ok().build();
    }

    // 마이 북마크인지 조회
    @ApiResponse(responseCode = "200", description = "마이 북마크인지 조회 성공", content = @Content(schema = @Schema(implementation =
            IsMyBookmarkResponse.class)))
    @GetMapping("study/{studyInfoId}/my-bookmark")
    public ResponseEntity<IsMyBookmarkResponse> getIsMyBookmark(@AuthenticationPrincipal User user,
                                                                @PathVariable(name = "studyInfoId") Long studyInfoId) {
        UserInfoResponse userInfo = authService.findUserInfo(user);
        return ResponseEntity.ok().body(studyBookmarkService.getIsMyBookMark(userInfo.getUserId(), studyInfoId));
    }
}
