package com.example.backend.study.api.controller.comment.commit;

import com.example.backend.auth.api.controller.auth.response.UserInfoResponse;
import com.example.backend.auth.api.service.auth.AuthService;
import com.example.backend.common.exception.GitudyException;
import com.example.backend.common.response.JsonResult;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.study.api.controller.comment.commit.request.AddCommitCommentRequest;
import com.example.backend.study.api.controller.comment.commit.response.CommitCommentInfoResponse;
import com.example.backend.study.api.service.comment.commit.CommitCommentService;
import com.example.backend.study.api.service.member.StudyMemberService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/commits")
public class CommitCommentController {
    private final CommitCommentService commitCommentService;
    private final StudyMemberService studyMemberService;
    private final AuthService authService;

    @ApiResponse(responseCode = "200",
            description = "커밋 댓글 리스트 조회 성공",
            content = @Content(schema = @Schema(implementation = CommitCommentInfoResponse.class)))
    @GetMapping("/{commitId}/comments")
    public JsonResult<?> commitCommentList(@AuthenticationPrincipal User user,
                                           @RequestParam(name = "studyInfoId") Long studyInfoId,
                                           @PathVariable(name = "commitId") Long commitId) {

        studyMemberService.isValidateStudyMember(user, studyInfoId);

        return JsonResult.successOf(commitCommentService.getCommitCommentsList(commitId));

    }

    @ApiResponse(responseCode = "200", description = "커밋 댓글 등록 성공")
    @PostMapping("/{commitId}/comments")
    public JsonResult<?> addCommitComment(@AuthenticationPrincipal User user,
                                          @PathVariable(name = "commitId") Long commitId,
                                          @Valid @RequestBody AddCommitCommentRequest request) {

        // 댓글을 달 수 있는 권한이 있는지 확인 (활동중인 스터디원인지 판단)
        UserInfoResponse userInfo = studyMemberService.isValidateStudyMember(user, commitId);

        // 댓글 등록
        commitCommentService.addCommitComment(userInfo.getUserId(), commitId, request);

        return JsonResult.successOf("댓글 작성에 성공하였습니다.");

    }

}
