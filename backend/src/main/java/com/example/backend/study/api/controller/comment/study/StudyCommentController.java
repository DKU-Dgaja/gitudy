package com.example.backend.study.api.controller.comment.study;

import com.example.backend.auth.api.service.auth.AuthService;
import com.example.backend.common.response.JsonResult;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.study.api.controller.comment.study.request.StudyCommentRegisterRequest;
import com.example.backend.study.api.controller.comment.study.request.StudyCommentUpdateRequest;
import com.example.backend.study.api.controller.info.request.StudyInfoUpdateRequest;
import com.example.backend.study.api.service.comment.study.StudyCommentService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/study")
public class StudyCommentController {
    private final StudyCommentService studyCommentService;
    private final AuthService authService;

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
}