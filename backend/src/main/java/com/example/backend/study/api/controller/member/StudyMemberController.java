package com.example.backend.study.api.controller.member;

import com.example.backend.auth.api.controller.auth.response.UserInfoResponse;
import com.example.backend.auth.api.service.auth.AuthService;
import com.example.backend.common.response.JsonResult;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.study.api.controller.member.request.MessageRequest;
import com.example.backend.study.api.controller.member.response.StudyMemberApplyListAndCursorIdxResponse;
import com.example.backend.study.api.controller.member.response.StudyMembersResponse;
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
    @PatchMapping("/{studyInfoId}/withdrawal")
    public JsonResult<?> withdrawalStudyMember(@AuthenticationPrincipal User user,
                                               @PathVariable(name = "studyInfoId") Long studyInfoId) {

        // 스터디멤버 검증
        UserInfoResponse userInfo = studyMemberService.isValidateStudyMember(user, studyInfoId);

        studyMemberService.withdrawalStudyMember(studyInfoId, userInfo);

        return JsonResult.successOf("Withdrawal Member Success");
    }

    // 스터디 가입 신청
    @ApiResponse(responseCode = "200", description = "스터디 가입 신청 성공")
    @PostMapping("/{studyInfoId}/apply")
    public JsonResult<?> applyStudyMember(@AuthenticationPrincipal User user,
                                          @PathVariable(name = "studyInfoId") Long studyInfoId,
                                          @RequestParam(name = "joinCode", required = false) String joinCode,
                                          @Valid @RequestBody MessageRequest messageRequest) {

        UserInfoResponse userInfo = authService.findUserInfo(user);

        studyMemberService.applyStudyMember(userInfo, studyInfoId, joinCode, messageRequest);

        return JsonResult.successOf("Apply StudyMember Success");
    }


    // 스터디 가입 신청 취소
    @ApiResponse(responseCode = "200", description = "스터디 가입 신청 취소 성공")
    @DeleteMapping("/{studyInfoId}/apply")
    public JsonResult<?> applyCancelStudyMember(@AuthenticationPrincipal User user,
                                                @PathVariable(name = "studyInfoId") Long studyInfoId) {

        UserInfoResponse userInfo = authService.findUserInfo(user);

        studyMemberService.applyCancelStudyMember(userInfo, studyInfoId);

        return JsonResult.successOf("Apply cancel StudyMember Success");
    }

    // 스터디장의 가입 신청 승인/거부
    @ApiResponse(responseCode = "200", description = "스터디 가입 승인/거부 성공")
    @PatchMapping("/{studyInfoId}/apply/{applyUserId}")
    public JsonResult<?> leaderApproveRefuseMember(@AuthenticationPrincipal User user,
                                                   @PathVariable(name = "studyInfoId") Long studyInfoId,
                                                   @PathVariable(name = "applyUserId") Long applyUserId,
                                                   @RequestParam(name = "approve", defaultValue = "false") boolean approve) {

        studyMemberService.isValidateStudyLeader(user, studyInfoId);

        studyMemberService.leaderApproveRefuseMember(studyInfoId, applyUserId, approve);

        return JsonResult.successOf("Apply Approve or Refuse StudyMember Success");

    }

    // 스터디 가입신청 목록 조회
    @ApiResponse(responseCode = "200", description = "스터디 가입신청 목록 조회 성공", content = @Content(schema = @Schema(implementation = StudyMemberApplyListAndCursorIdxResponse.class)))
    @GetMapping("/{studyInfoId}/apply")
    public JsonResult<?> applyListStudyMember(@AuthenticationPrincipal User user,
                                              @PathVariable(name = "studyInfoId") Long studyInfoId,
                                              @Min(value = 0, message = "Cursor index cannot be negative") @RequestParam(name = "cursorIdx", required = false) Long cursorIdx,
                                              @Min(value = 1, message = "Limit cannot be less than 1") @RequestParam(name = "limit", defaultValue = "3") Long limit) {

        studyMemberService.isValidateStudyLeader(user, studyInfoId);

        return JsonResult.successOf(studyMemberService.applyListStudyMember(studyInfoId, cursorIdx, limit));
    }


    // 스터디 멤버에게 알림
    @ApiResponse(responseCode = "200", description = "멤버에게 알림 전송 성공")
    @PostMapping("/{studyInfoId}/notify/{notifyUserId}")
    public JsonResult<?> notifyToStudyMember(@AuthenticationPrincipal User user,
                                             @PathVariable("studyInfoId") Long studyInfoId,
                                             @PathVariable("notifyUserId") Long notifyUserId,
                                             @Valid @RequestBody MessageRequest messageRequest) {

        studyMemberService.isValidateStudyLeader(user, studyInfoId);

        studyMemberService.notifyToStudyMember(studyInfoId, notifyUserId, messageRequest);
        return JsonResult.successOf("Notify to StudyMember Success");
    }

    // 스터디 멤버가 팀장에게 알림
    @ApiResponse(responseCode = "200", description = "팀장에게 알림 전송 성공")
    @PostMapping("/{studyInfoId}/notify/leader")
    public JsonResult<?> notifyToStudyLeader(@AuthenticationPrincipal User user,
                                             @PathVariable("studyInfoId") Long studyInfoId,
                                             @Valid @RequestBody MessageRequest messageRequest) {

        UserInfoResponse userInfo = studyMemberService.isValidateStudyMember(user, studyInfoId);

        studyMemberService.notifyToStudyLeader(studyInfoId, userInfo, messageRequest);
        return JsonResult.successOf("Notify to StudyLeader Success");
    }
}
