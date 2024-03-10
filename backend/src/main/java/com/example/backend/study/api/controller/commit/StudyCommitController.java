package com.example.backend.study.api.controller.commit;

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
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StudyCommitController {
    private final StudyCommitService studyCommitService;
    private final StudyMemberService studyMemberService;
    private final AuthService authService;

    @ApiResponse(responseCode = "200",
            description = "마이 커밋 조회 성공",
            content = @Content(schema = @Schema(implementation = CommitInfoListAndCursorIdxResponse.class)))
    @GetMapping("/commit/user/{userId}")
    public JsonResult<?> userCommitList(@AuthenticationPrincipal User user,
                                        @PathVariable(name = "userId") Long userId,
                                        @RequestParam(name = "filterStudyId", required = false) Long filterStudyId,
                                        @Min(value = 0, message = "Cursor index cannot be negative") @RequestParam(name = "cursorIdx") Long cursorIdx,
                                        @Min(value = 1, message = "Limit cannot be less than 1") @RequestParam(name = "limit", defaultValue = "20") Long limit) {

        authService.authenticate(userId, user);

        List<CommitInfoResponse> commitInfoList = studyCommitService.selectUserCommitList(userId, filterStudyId, cursorIdx, limit);

        CommitInfoListAndCursorIdxResponse response = CommitInfoListAndCursorIdxResponse.builder()
                .commitInfoList(commitInfoList)
                .build();

        response.setNextCursorIdx();

        return JsonResult.successOf(response);
    }

    @ApiResponse(responseCode = "200", description = "커밋 상세 페이지 조회 성공",
            content = @Content(schema = @Schema(implementation = CommitInfoResponse.class)))
    @GetMapping("/study/{studyId}/commit/{commitId}")
    public JsonResult<?> commitDetails(@AuthenticationPrincipal User user,
                                       @PathVariable(name = "studyId") Long studyId,
                                       @PathVariable(name = "commitId") Long commitId) {

        studyMemberService.isValidateStudyMember(user, studyId);

        return JsonResult.successOf(studyCommitService.getCommitDetailsById(commitId));
    }

    @ApiResponse(responseCode = "200", description = "특정 스터디의 커밋 정보 업데이트(저장) 성공")
    @GetMapping("/study/{studyInfoId}/commit/pull")
    public JsonResult<?> pullCommits(@PathVariable(name = "studyInfoId") Long studyInfoId) {

        studyCommitService.pullCommitsFromGitHub(studyInfoId);

        return JsonResult.successOf("깃허브로부터 해당 스터디의 커밋을 업데이트 완료하였습니다.");
    }
}
