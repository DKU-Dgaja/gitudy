package com.example.backend.study.api.controller.convention;

import com.example.backend.common.response.JsonResult;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.study.api.controller.convention.request.StudyConventionRequest;
import com.example.backend.study.api.controller.convention.request.StudyConventionUpdateRequest;
import com.example.backend.study.api.service.convention.StudyConventionService;
import com.example.backend.study.api.service.member.StudyMemberService;
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
public class StudyConventionController {

    private final StudyConventionService studyConventionService;
    private final StudyMemberService studyMemberService;


    @ApiResponse(responseCode = "200", description = "컨벤션 등록 성공")
    @PostMapping("/{studyInfoId}/convention")
    public JsonResult<?> registerStudyConvention(@AuthenticationPrincipal User user,
                                                 @PathVariable(name = "studyInfoId") Long studyInfoId,
                                                 @Valid @RequestBody StudyConventionRequest studyConventionRequest) {


        studyMemberService.isValidateStudyLeader(user, studyInfoId);

        studyConventionService.registerStudyConvention(studyConventionRequest, studyInfoId);

        return JsonResult.successOf("StudyConvention register Success");
    }


    @ApiResponse(responseCode = "200", description = "컨벤션 수정 성공")
    @PutMapping("/{studyInfoId}/convention/{conventionId}")
    public JsonResult<?> updateStudyConvention(@AuthenticationPrincipal User user,
                                               @PathVariable(name = "studyInfoId") Long studyInfoId,
                                               @PathVariable(name = "conventionId") Long conventionId,
                                               @Valid @RequestBody StudyConventionUpdateRequest studyConventionUpdateRequest) {

        studyMemberService.isValidateStudyLeader(user, studyInfoId);

        studyConventionService.updateStudyConvention(studyConventionUpdateRequest, conventionId);

        return JsonResult.successOf("StudyConvention update Success");
    }

    @ApiResponse(responseCode = "200", description = "컨벤션 삭제 성공")
    @DeleteMapping("/{studyInfoId}/convention/{conventionId}")
    public JsonResult<?> deleteStudyConvention(@AuthenticationPrincipal User user,
                                               @PathVariable(name = "studyInfoId") Long studyInfoId,
                                               @PathVariable(name = "conventionId") Long conventionId) {

        studyMemberService.isValidateStudyLeader(user, studyInfoId);

        studyConventionService.deleteStudyConvention(conventionId);

        return JsonResult.successOf("StudyConvention delete Success");
    }
}
