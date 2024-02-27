package com.example.backend.study.api.controller.member;

import com.example.backend.auth.api.service.auth.AuthService;
import com.example.backend.common.exception.GitudyException;
import com.example.backend.common.response.JsonResult;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.study.info.constant.StudyStatus;
import com.example.backend.study.api.controller.member.response.StudyMembersResponse;
import com.example.backend.study.api.service.info.StudyInfoService;
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
    private final StudyInfoService studyInfoService;
    private final AuthService authService;


    // 스터디에 속한 스터디원 조회
    @ApiResponse(responseCode = "200", description = "스터디원 조회 성공", content = @Content(schema = @Schema(implementation = StudyMembersResponse.class)))
    @GetMapping("/{studyInfoId}")
    public JsonResult<?> readStudyMembers(@AuthenticationPrincipal User user,
                                          @PathVariable(name = "studyInfoId") Long studyInfoId) {


        authService.authenticate(user.getId(), user);

        // 스터디 상태 확인
        StudyStatus studyStatus = studyInfoService.isValidateStudyStatus(studyInfoId);

        // 공개 스터디인 경우
        if (studyStatus == StudyStatus.STUDY_PUBLIC) {

            return JsonResult.successOf(studyMemberService.readStudyMembers(studyInfoId));

        } else if (studyStatus == StudyStatus.STUDY_PRIVATE) { // 비공개 스터디인 경우

            try {
                // 스터디원인지 확인
                studyMemberService.isValidateStudyMember(user, studyInfoId);
            } catch (GitudyException e) {

                // 스터디원이 아니고 비공개 스터디인 경우
                return JsonResult.failOf("해당 스터디원이 아닙니다.");
            }

            // 비공개 스터디이지만 스터디원인 경우
            return JsonResult.successOf(studyMemberService.readStudyMembers(studyInfoId));

        }
        else { // 삭제 스터디인 경우

            return JsonResult.failOf("해당 스터디는 삭제되었습니다.");
        }

    }
}
