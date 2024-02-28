package com.example.backend.study.api.controller.member;

import com.example.backend.auth.api.service.auth.AuthService;
import com.example.backend.common.response.JsonResult;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.study.api.controller.member.response.StudyMembersResponse;
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
@RequestMapping("/member")
public class StudyMemberController {

    private final StudyMemberService studyMemberService;
    private final AuthService authService;

    // 스터디에 속한 스터디원 조회 (기여도별 조회)
    @ApiResponse(responseCode = "200", description = "스터디원 조회 성공", content = @Content(schema = @Schema(implementation = StudyMembersResponse.class)))
    @GetMapping("/{studyInfoId}")
    public JsonResult<?> readStudyMembers(@AuthenticationPrincipal User user,
                                          @PathVariable(name = "studyInfoId") Long studyInfoId,
                                          @RequestParam("userId") Long userId) {

        authService.authenticate(userId, user);

        return JsonResult.successOf(studyMemberService.readStudyMembers(studyInfoId));
    }
}
