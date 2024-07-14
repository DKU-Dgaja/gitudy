package com.example.backend.study.api.controller.comment.study;

import com.example.backend.auth.api.controller.auth.response.UserInfoResponse;
import com.example.backend.auth.api.service.auth.AuthService;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.study.api.controller.comment.study.request.StudyCommentRegisterRequest;
import com.example.backend.study.api.controller.comment.study.request.StudyCommentUpdateRequest;
import com.example.backend.study.api.controller.comment.study.response.StudyCommentListAndCursorIdxResponse;
import com.example.backend.study.api.service.comment.study.StudyCommentService;
import com.example.backend.study.api.service.member.StudyMemberService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Void> registerStudyComment(@AuthenticationPrincipal User user,
                                                  @PathVariable("studyInfoId") Long studyInfoId,
                                                  @Valid @RequestBody StudyCommentRegisterRequest studyCommentRegisterRequest) {
        UserInfoResponse userInfo = studyMemberService.isValidateStudyMember(user, studyInfoId);
        studyCommentService.registerStudyComment(studyCommentRegisterRequest, studyInfoId, userInfo.getUserId());

        return ResponseEntity.ok().build();
    }

    @ApiResponse(responseCode = "200", description = "스터디 댓글 수정 성공")
    @PatchMapping("/{studyInfoId}/comment/{studyCommentId}")
    public ResponseEntity<Void> updateStudyComment(@AuthenticationPrincipal User user,
                                                @PathVariable(name = "studyInfoId") Long studyInfoId,
                                                @PathVariable(name = "studyCommentId") Long studyCommentId,
                                                @Valid @RequestBody StudyCommentUpdateRequest studyCommentUpdateRequest) {
        UserInfoResponse userInfo = studyMemberService.isValidateStudyMember(user, studyInfoId);
        studyCommentService.updateStudyComment(studyCommentUpdateRequest, studyInfoId, studyCommentId, userInfo.getUserId());

        return ResponseEntity.ok().build();
    }

    @ApiResponse(responseCode = "200", description = "스터디 댓글 삭제 성공")
    @DeleteMapping("/{studyInfoId}/comment/{studyCommentId}")
    public ResponseEntity<Void> deleteStudyComment(@AuthenticationPrincipal User user,
                                                @PathVariable(name = "studyInfoId") Long studyInfoId,
                                                @PathVariable(name = "studyCommentId") Long studyCommentId
    ) {
        studyMemberService.isValidateStudyMember(user, studyInfoId);
        studyCommentService.deleteStudyComment(user, studyInfoId, studyCommentId);

        return ResponseEntity.ok().build();
    }

    @ApiResponse(responseCode = "200",
            description = "스터디 댓글 조회 성공",
            content = @Content(schema = @Schema(implementation = StudyCommentListAndCursorIdxResponse.class)))
    @GetMapping("/{studyInfoId}/comments")
    public ResponseEntity<?> StudyCommentList(@AuthenticationPrincipal User user,
                                              @PathVariable(name = "studyInfoId") Long studyInfoId,
                                              @Min(value = 0, message = "Cursor index cannot be negative") @RequestParam(name = "cursorIdx", required = false) Long cursorIdx,
                                              @Min(value = 1, message = "Limit cannot be less than 1") @RequestParam(name = "limit", defaultValue = "5") Long limit) {

        studyMemberService.isValidateStudyMember(user, studyInfoId);
        StudyCommentListAndCursorIdxResponse response = studyCommentService.selectStudyCommentList(studyInfoId, cursorIdx, limit);

        return ResponseEntity.ok().body(response);
    }
}
