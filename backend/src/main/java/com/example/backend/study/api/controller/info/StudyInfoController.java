package com.example.backend.study.api.controller.info;

import com.example.backend.auth.api.service.auth.AuthService;
import com.example.backend.common.exception.GitudyException;
import com.example.backend.common.response.JsonResult;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.study.api.controller.info.request.StudyInfoRegisterRequest;
import com.example.backend.study.api.controller.info.request.StudyInfoUpdateRequest;
import com.example.backend.study.api.controller.info.response.MyStudyInfoListAndCursorIdxResponse;
import com.example.backend.study.api.controller.info.response.MyStudyInfoListResponse;
import com.example.backend.study.api.controller.info.response.StudyInfoRegisterResponse;
import com.example.backend.study.api.service.info.StudyInfoService;
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
public class StudyInfoController {
    private final StudyInfoService studyInfoService;
    private final AuthService authService;
    private final StudyMemberService studyMemberService;

    @ApiResponse(responseCode = "200", description = "스터디 등록 성공")
    @PostMapping("/")
    public JsonResult<?> registerStudy(@AuthenticationPrincipal User user,
                                       @Valid @RequestBody StudyInfoRegisterRequest studyInfoRequest) {
        authService.authenticate(studyInfoRequest.getUserId(), user);
        StudyInfoRegisterResponse response = studyInfoService.registerStudy(studyInfoRequest);
        return JsonResult.successOf("Study Register Success.");
    }

    @ApiResponse(responseCode = "200", description = "스터디 삭제 성공")
    @DeleteMapping("/{studyInfoId}")
    public JsonResult<?> deleteStudy(@AuthenticationPrincipal User user,
                                     @PathVariable(name = "studyInfoId") Long studyInfoId) {
        try {
            // 유저가 스터디 장이 아닐 경우 예외 발생
            studyMemberService.isValidateStudyLeader(user, studyInfoId);
            studyInfoService.deleteStudy(studyInfoId);

        } catch (GitudyException e) {
            return JsonResult.failOf(e.getMessage());
        }
        return JsonResult.successOf("Study deleted successfully");
    }

    @ApiResponse(responseCode = "200", description = "스터디 정보 수정 성공")
    @PatchMapping("/{studyInfoId}")
    public JsonResult<?> updateStudyInfo(@AuthenticationPrincipal User user,
                                         @PathVariable(name = "studyInfoId") Long studyInfoId,
                                         @Valid @RequestBody StudyInfoUpdateRequest studyInfoUpdateRequest) {

        // 리더인지 확인
        studyMemberService.isValidateStudyLeader(user, studyInfoId);

        studyInfoService.updateStudyInfo(studyInfoUpdateRequest, studyInfoId);

        return JsonResult.successOf("StudyInfo update Success");
    }

    @ApiResponse(responseCode = "200", description = "스터디 정보 수정 페이지 요청 성공")
    @GetMapping("/{studyInfoId}/update")
    public JsonResult<?> updateStudyInfoPage(@AuthenticationPrincipal User user,
                                             @PathVariable(name = "studyInfoId") Long studyInfoId) {
        // 리더인지 확인
        studyMemberService.isValidateStudyLeader(user, studyInfoId);
        return JsonResult.successOf(studyInfoService.updateStudyInfoPage(studyInfoId));
    }

    // 마이 스터디 조회
    @ApiResponse(responseCode = "200", description = "마이 스터디 조회 성공", content = @Content(schema = @Schema(implementation =
                    MyStudyInfoListResponse.class)))
    @GetMapping("/{userId}")
    public JsonResult<?> userStudyInfoListByParameter(@AuthenticationPrincipal User user,
                                                      @PathVariable(name = "userId") Long userId,
                                                      @Min(value = 0, message = "Cursor index cannot be negative")
                                                      @RequestParam(name = "cursorIdx") Long cursorIdx,
                                                      @RequestParam(name = "limit") Long limit,
                                                      @RequestParam(name = "sortBy") String sortBy
    ) {
        authService.authenticate(userId, user);
        return JsonResult.successOf(studyInfoService.selectMyStudyInfoList(userId, cursorIdx, limit, sortBy));
    }
}