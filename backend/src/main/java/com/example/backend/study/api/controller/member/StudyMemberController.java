package com.example.backend.study.api.controller.member;

import com.example.backend.auth.api.controller.auth.response.UserInfoResponse;
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
                                          @RequestParam(name = "orderByScore", defaultValue = "false") boolean orderByScore) {

        authService.findUserInfo(user);

        return JsonResult.successOf(studyMemberService.readStudyMembers(studyInfoId, orderByScore));
    }

    // 스터디원 강퇴
    @ApiResponse(responseCode = "200", description = "스터디원 강퇴 성공")
    @PatchMapping("/{studyInfoId}/resign/{resignUserId}")
    public JsonResult<?> resignStudyMember(@AuthenticationPrincipal User user,
                                           @PathVariable(name = "studyInfoId") Long studyInfoId,
                                           @PathVariable(name = "resignUserId") Long resignUserId) {

        // 스터디장 검증
        studyMemberService.isValidateStudyLeader(user, studyInfoId);

        studyMemberService.resignStudyMember(studyInfoId, resignUserId);

        return JsonResult.successOf("Resign Member Success");
    }

    // 스터디 탈퇴
    @ApiResponse(responseCode = "200", description = "스터디 탈퇴 성공")
    @PatchMapping("/{studyInfoId}/withdrawal/{userId}")
    public JsonResult<?> withdrawalStudyMember(@AuthenticationPrincipal User user,
                                               @PathVariable(name = "studyInfoId") Long studyInfoId,
                                               @PathVariable(name = "userId") Long userId) {

        // 스터디멤버 검증
        studyMemberService.isValidateStudyMember(user, studyInfoId);

        studyMemberService.withdrawalStudyMember(studyInfoId, userId);

        return JsonResult.successOf("Withdrawal Member Success");
    }

    // 스터디 가입 신청
    @ApiResponse(responseCode = "200", description = "스터디 가입 신청 성공")
    @PostMapping("/{studyInfoId}/apply")
    public JsonResult<?> applyStudyMember(@AuthenticationPrincipal User user,
                                          @PathVariable(name = "studyInfoId") Long studyInfoId) {

        UserInfoResponse userInfo = authService.findUserInfo(user);

        studyMemberService.applyStudyMember(userInfo, studyInfoId);

        return JsonResult.successOf("Apply StudyMember Success");
    }


}
