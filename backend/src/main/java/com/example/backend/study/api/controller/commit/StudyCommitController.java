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
    @PostMapping("/user/{userId}")
    public JsonResult<?> userCommitList(@AuthenticationPrincipal User user,
                                        @PathVariable(name = "userId") Long userId,
                                        @Min(value = 0, message = "Cursor index cannot be negative") @RequestParam(name = "cursorIdx") Long cursorIdx) {

        authService.authenticate(userId, user);

        List<CommitInfoResponse> commitInfoList = studyCommitService.selectUserCommitList(userId, cursorIdx);

        // 다음 cursorIdx
        Long nextCursorIdx = null;
        if (!commitInfoList.isEmpty()) {
            nextCursorIdx = commitInfoList.get(commitInfoList.size() - 1).getId();
        }

        return JsonResult.successOf(CommitInfoListAndCursorIdxResponse.builder()
                .commitInfoList(commitInfoList)
                .cursorIdx(nextCursorIdx)
                .build());
    }
}
