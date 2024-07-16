package com.example.backend.study.api.controller.convention;

import com.example.backend.domain.define.account.user.User;
import com.example.backend.study.api.controller.convention.request.StudyConventionRequest;
import com.example.backend.study.api.controller.convention.request.StudyConventionUpdateRequest;
import com.example.backend.study.api.controller.convention.response.StudyConventionListAndCursorIdxResponse;
import com.example.backend.study.api.controller.convention.response.StudyConventionResponse;
import com.example.backend.study.api.service.convention.StudyConventionService;
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
public class StudyConventionController {

    private final StudyConventionService studyConventionService;
    private final StudyMemberService studyMemberService;


    @ApiResponse(responseCode = "200", description = "컨벤션 등록 성공")
    @PostMapping("/{studyInfoId}/convention")
    public ResponseEntity<Void> registerStudyConvention(@AuthenticationPrincipal User user,
                                                        @PathVariable(name = "studyInfoId") Long studyInfoId,
                                                        @Valid @RequestBody StudyConventionRequest studyConventionRequest) {


        studyMemberService.isValidateStudyLeader(user, studyInfoId);

        studyConventionService.registerStudyConvention(studyConventionRequest, studyInfoId);

        return ResponseEntity.ok().build();
    }


    @ApiResponse(responseCode = "200", description = "컨벤션 수정 성공")
    @PutMapping("/{studyInfoId}/convention/{conventionId}")
    public ResponseEntity<Void> updateStudyConvention(@AuthenticationPrincipal User user,
                                                      @PathVariable(name = "studyInfoId") Long studyInfoId,
                                                      @PathVariable(name = "conventionId") Long conventionId,
                                                      @Valid @RequestBody StudyConventionUpdateRequest studyConventionUpdateRequest) {

        studyMemberService.isValidateStudyLeader(user, studyInfoId);

        studyConventionService.updateStudyConvention(studyConventionUpdateRequest, conventionId);

        return ResponseEntity.ok().build();
    }

    @ApiResponse(responseCode = "200", description = "컨벤션 삭제 성공")
    @DeleteMapping("/{studyInfoId}/convention/{conventionId}")
    public ResponseEntity<Void> deleteStudyConvention(@AuthenticationPrincipal User user,
                                                      @PathVariable(name = "studyInfoId") Long studyInfoId,
                                                      @PathVariable(name = "conventionId") Long conventionId) {

        studyMemberService.isValidateStudyLeader(user, studyInfoId);

        studyConventionService.deleteStudyConvention(conventionId);

        return ResponseEntity.ok().build();
    }

    @ApiResponse(responseCode = "200", description = "컨벤션 조회 성공", content = @Content(schema = @Schema(implementation = StudyConventionResponse.class)))
    @GetMapping("/{studyInfoId}/convention/{conventionId}")
    public ResponseEntity<StudyConventionResponse> readStudyConvention(@AuthenticationPrincipal User user,
                                                                       @PathVariable(name = "studyInfoId") Long studyInfoId,
                                                                       @PathVariable(name = "conventionId") Long conventionId) {

        studyMemberService.isValidateStudyMember(user, studyInfoId);

        return ResponseEntity.ok().body(studyConventionService.readStudyConvention(conventionId));
    }

    @ApiResponse(responseCode = "200", description = "컨벤션 전체조회 성공", content = @Content(schema = @Schema(implementation = StudyConventionListAndCursorIdxResponse.class)))
    @GetMapping("/{studyInfoId}/convention")
    public ResponseEntity<StudyConventionListAndCursorIdxResponse> readStudyConventionList(@AuthenticationPrincipal User user,
                                                                                           @PathVariable(name = "studyInfoId") Long studyInfoId,
                                                                                           @Min(value = 0, message = "Cursor index cannot be negative") @RequestParam(name = "cursorIdx", required = false) Long cursorIdx,
                                                                                           @Min(value = 1, message = "Limit cannot be less than 1") @RequestParam(name = "limit", defaultValue = "4") Long limit) {

        studyMemberService.isValidateStudyMember(user, studyInfoId);

        return ResponseEntity.ok().body(studyConventionService.readStudyConventionList(studyInfoId, cursorIdx, limit));
    }

}
