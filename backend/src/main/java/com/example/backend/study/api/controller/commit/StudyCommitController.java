package com.example.backend.study.api.controller.commit;

import com.example.backend.auth.api.controller.auth.response.UserInfoResponse;
import com.example.backend.auth.api.service.auth.AuthService;
import com.example.backend.common.response.JsonResult;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.study.api.controller.commit.response.CommitInfoListAndCursorIdxResponse;
import com.example.backend.study.api.service.commit.StudyCommitService;
import com.example.backend.study.api.service.commit.response.CommitInfoResponse;
import com.example.backend.study.api.service.member.StudyMemberService;
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
    private final StudyMemberService studyMemberService;
    private final AuthService authService;

    @ApiResponse(responseCode = "200",
            description = "마이 커밋 조회 성공",
            content = @Content(schema = @Schema(implementation = CommitInfoListAndCursorIdxResponse.class)))
    @GetMapping("")
    public JsonResult<?> userCommitList(@AuthenticationPrincipal User user,
                                        @RequestParam(name = "studyInfoId", required = false) Long studyInfoId,
                                        @Min(value = 0, message = "Cursor index cannot be negative") @RequestParam(name = "cursorIdx", required = false) Long cursorIdx,
                                        @Min(value = 1, message = "Limit cannot be less than 1") @RequestParam(name = "limit", defaultValue = "20") Long limit) {

        UserInfoResponse userInfo = authService.findUserInfo(user);

        List<CommitInfoResponse> commitInfoList = studyCommitService.selectUserCommitList(userInfo.getUserId(), studyInfoId, cursorIdx, limit);

        CommitInfoListAndCursorIdxResponse response = CommitInfoListAndCursorIdxResponse.builder()
                .commitInfoList(commitInfoList)
                .build();

        response.setNextCursorIdx();

        return JsonResult.successOf(response);
    }

    @ApiResponse(responseCode = "200", description = "커밋 상세 페이지 조회 성공",
            content = @Content(schema = @Schema(implementation = CommitInfoResponse.class)))
    @GetMapping("/{commitId}")
    public JsonResult<?> commitDetails(@AuthenticationPrincipal User user,
                                       @RequestParam(name = "studyInfoId") Long studyInfoId,
                                       @PathVariable(name = "commitId") Long commitId) {

        studyMemberService.isValidateStudyMember(user, studyInfoId);

        return JsonResult.successOf(studyCommitService.getCommitDetailsById(commitId));
    }

}
