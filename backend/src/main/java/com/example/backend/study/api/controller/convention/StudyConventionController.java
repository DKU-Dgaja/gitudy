package com.example.backend.study.api.controller.convention;

import com.example.backend.common.response.JsonResult;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.study.api.controller.convention.request.StudyConventionRequest;
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
@RequestMapping("/convention")
public class StudyConventionController {

    private final StudyConventionService studyConventionService;
    private final StudyMemberService studyMemberService;


    @ApiResponse(responseCode = "200", description = "컨벤션 등록 성공")
    @PostMapping("/{studyInfoId}")
    public JsonResult<?> registerStudyConvention(@AuthenticationPrincipal User user,
                                                 @PathVariable("studyInfoId") Long studyInfoId,
                                                 @Valid @RequestBody StudyConventionRequest studyConventionRequest) {

        studyMemberService.isValidateStudyLeader(user, studyInfoId);

        studyConventionService.registerStudyConvention(studyConventionRequest, studyInfoId);

        return JsonResult.successOf("StudyConvention register Success");
    }
}
