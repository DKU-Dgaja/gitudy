package com.example.backend.study.api.controller.comment.commit;

import com.example.backend.common.response.JsonResult;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.study.api.controller.comment.commit.response.CommitCommentInfoResponse;
import com.example.backend.study.api.service.comment.commit.CommitCommentService;
import com.example.backend.study.api.service.member.StudyMemberService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

}
