package com.example.backend.study.api.controller.comment.study;

import com.example.backend.auth.api.service.auth.AuthService;
import com.example.backend.common.response.JsonResult;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.study.comment.study.StudyComment;
import com.example.backend.study.api.controller.comment.study.request.StudyCommentRegisterRequest;
import com.example.backend.study.api.controller.comment.study.request.StudyCommentUpdateRequest;
import com.example.backend.study.api.controller.comment.study.response.StudyCommentListAndCursorIdxResponse;
import com.example.backend.study.api.controller.comment.study.response.StudyCommentResponse;
import com.example.backend.study.api.service.comment.study.StudyCommentService;
import com.example.backend.study.api.service.member.StudyMemberService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/study")
public class StudyCommentController {
    private final StudyCommentService studyCommentService;

    private final AuthService authService;

    private final StudyMemberService studyMemberService;

    @ApiResponse(responseCode = "200", description = "스터디 댓글 등록 성공")
    @PostMapping("/{studyInfoId}/comment")
    public JsonResult<?> registerStudyComment(@AuthenticationPrincipal User user,
                                           @PathVariable("studyInfoId") Long studyInfoId,
                                           @Valid @RequestBody StudyCommentRegisterRequest studyCommentRegisterRequest) {
        authService.authenticate(studyCommentRegisterRequest.getUserId(), user);
        studyCommentService.registerStudyComment(studyCommentRegisterRequest, studyInfoId);

        return JsonResult.successOf("StudyComment register Success");
    }

    @ApiResponse(responseCode = "200", description = "스터디 댓글 수정 성공")
    @PatchMapping("/{studyInfoId}/comment/{studyCommentId}")
    public JsonResult<?> updateStudyComment(@AuthenticationPrincipal User user,
                                         @PathVariable(name = "studyInfoId") Long studyInfoId,
                                         @PathVariable(name = "studyCommentId") Long studyCommentId,
                                         @Valid @RequestBody StudyCommentUpdateRequest studyCommentUpdateRequest) {
        authService.authenticate(studyCommentUpdateRequest.getUserId(), user);
        studyCommentService.updateStudyComment(studyCommentUpdateRequest, studyCommentId);

        return JsonResult.successOf("StudyComment update Success");
    }
    @ApiResponse(responseCode = "200", description = "스터디 댓글 삭제 성공")
    @DeleteMapping("/{studyInfoId}/comment/{studyCommentId}")
    public JsonResult<?> deleteStudyComment(@AuthenticationPrincipal User user,
                                     @PathVariable(name = "studyInfoId") Long studyInfoId,
                                     @PathVariable(name = "studyCommentId") Long studyCommentId
    ) {
        studyMemberService.isValidateStudyMember(user, studyInfoId);
        studyCommentService.deleteStudyComment(user, studyInfoId, studyCommentId);

        return JsonResult.successOf("StudyComment deleted successfully");
    }

    @ApiResponse(responseCode = "200",
            description = "스터디 댓글 조회 성공",
            content = @Content(schema = @Schema(implementation = StudyCommentListAndCursorIdxResponse.class)))
    @GetMapping("/{studyInfoId}/comments")
    public JsonResult<?> StudyCommentList(@AuthenticationPrincipal User user,
                                          @PathVariable(name = "studyInfoId") Long studyInfoId,
                                          @Min(value = 0, message = "Cursor index cannot be negative") @RequestParam(name = "cursorIdx") Long cursorIdx,
                                          @Min(value = 1, message = "Limit cannot be less than 1") @RequestParam(name = "limit", defaultValue = "5") Long limit) {

        studyMemberService.isValidateStudyMember(user, studyInfoId);
        StudyCommentListAndCursorIdxResponse response = studyCommentService.selectStudyCommentList(studyInfoId, cursorIdx, limit);

        return JsonResult.successOf(response);
    }
}
