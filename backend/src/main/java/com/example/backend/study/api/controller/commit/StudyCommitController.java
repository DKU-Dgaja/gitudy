package com.example.backend.study.api.controller.commit;

import com.example.backend.auth.api.service.auth.AuthService;
import com.example.backend.common.response.JsonResult;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.study.api.controller.commit.response.CommitInfoListAndCursorIdxResponse;
import com.example.backend.study.api.service.StudyCommitService;
import com.example.backend.study.api.service.commit.response.CommitInfoResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/commits")
public class StudyCommitController {
    private final StudyCommitService studyCommitService;
    private final AuthService authService;

    @ApiResponse(responseCode = "200",
            description = "마이 커밋 조회 성공",
            content = @Content(schema = @Schema(implementation = CommitInfoListAndCursorIdxResponse.class)))
    @GetMapping("/user/{userId}")
    public JsonResult<?> userCommitList(@AuthenticationPrincipal User user,
                                        @PathVariable(name = "userId") Long userId,
                                        @Min(value = 0, message = "Cursor index cannot be negative") @RequestParam(name = "cursorIdx") Long cursorIdx,
                                        @Min(value = 1, message = "Limit cannot be less than 1") @RequestParam(name = "limit", defaultValue = "20") Long limit) {

        authService.authenticate(userId, user);

        List<CommitInfoResponse> commitInfoList = studyCommitService.selectUserCommitList(userId, cursorIdx, limit);

        // 다음 cursorIdx
        Long nextCursorIdx = 0L;
        if (!commitInfoList.isEmpty()) {
            nextCursorIdx = commitInfoList.get(commitInfoList.size() - 1).getId();
        }

        return JsonResult.successOf(CommitInfoListAndCursorIdxResponse.builder()
                .commitInfoList(commitInfoList)
                .cursorIdx(nextCursorIdx)
                .build());
    }

    @ApiResponse(responseCode = "200", description = "커밋 상세 페이지 조회 성공",
            content = @Content(schema = @Schema(implementation = CommitInfoResponse.class)))
    @GetMapping("/{commitId}")
    public JsonResult<?> commitDetails(@PathVariable(name = "commitId") Long commitId) {

        return JsonResult.successOf(studyCommitService.getCommitDetailsById(commitId));
    }
}
