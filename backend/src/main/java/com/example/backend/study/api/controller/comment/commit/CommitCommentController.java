package com.example.backend.study.api.controller.comment.commit;

import com.example.backend.common.response.JsonResult;
import com.example.backend.study.api.controller.comment.commit.response.CommitCommentInfoResponse;
import com.example.backend.study.api.controller.commit.response.CommitInfoListAndCursorIdxResponse;
import com.example.backend.study.api.service.comment.commit.CommitCommentService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/comments/commits")
public class CommitCommentController {
    private final CommitCommentService commitCommentService;

    @ApiResponse(responseCode = "200",
            description = "커밋 댓글 리스트 조회 성공",
            content = @Content(schema = @Schema(implementation = CommitCommentInfoResponse.class)))
    @GetMapping("/{commitId}")
    public JsonResult<?> commitCommentList(@PathVariable(name = "commitId") Long commitId) {
        return JsonResult.successOf(commitCommentService.getCommitCommentsList(commitId));
    }

}
