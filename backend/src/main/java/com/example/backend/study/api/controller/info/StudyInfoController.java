package com.example.backend.study.api.controller.info;

import com.example.backend.auth.api.service.auth.AuthService;
import com.example.backend.study.api.controller.info.request.StudyInfoRegisterRequest;
import com.example.backend.study.api.controller.info.response.*;
import com.example.backend.study.api.service.info.StudyInfoService;
import com.example.backend.common.response.JsonResult;
import com.example.backend.domain.define.account.user.User;
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
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/studyinfo")
public class StudyInfoController {

    private final StudyInfoService studyInfoService;
    private final AuthService authService;

    // 스터디 등록
    @PostMapping("/")
    public JsonResult<?> registerStudy(@AuthenticationPrincipal User user,
                                       @Valid @RequestBody StudyInfoRegisterRequest studyInfoRequest) {
        authService.authenticate(user);
        StudyInfoRegisterResponse response = studyInfoService.registerStudy(studyInfoRequest);
        return JsonResult.successOf("Study Register Success.");
    }

    // 한개의 스터디 상세정보 조회
    @GetMapping("/{studyInfoId}")
    public JsonResult<?> getStudyInfo(@AuthenticationPrincipal User user,
                                      @PathVariable(name = "studyInfoId") Long studyInfoId) {
        authService.authenticate(user);
        Optional<StudyInfoResponse> studyInfo = studyInfoService.selectStudyInfo(studyInfoId);
        return JsonResult.successOf(studyInfo);
    }

    // 모든 스터디 상세정보 조회
    @GetMapping("/all")
    public JsonResult<?> getAllStudyInfo(@AuthenticationPrincipal User user) {
        authService.authenticate(user);
        List<AllStudyInfoResponse> responses = studyInfoService.selectStudyInfoList();
        return JsonResult.successOf(responses);
    }

    // 스터디 삭제
    @DeleteMapping("/{studyInfoId}")
    public JsonResult<?> deleteStudy(@AuthenticationPrincipal User user,
                                     @PathVariable(name = "studyInfoId") Long studyInfoId) {
        authService.authenticate(user);
        studyInfoService.deleteStudy(studyInfoId);
        return JsonResult.successOf("Study deleted successfully");
    }

    // 마이 스터디 조회
    @ApiResponse(responseCode = "200",
            description = "마이 스터디 조회 성공",
            content = @Content(schema = @Schema(implementation =
                    StudyInfoListAndCursorIdxResponse.class)))
    @GetMapping("/user/{userId}")
    public JsonResult<?> userStudyInfoList(@AuthenticationPrincipal User user,
                                           @PathVariable(name = "userId") Long userId,
                                           @RequestParam(name = "limit") Long limit,
                                           @Min(value = 0, message = "Cursor index cannot be negative")
                                           @RequestParam(name = "cursorIdx") Long cursorIdx
    ) {
        authService.authenticate(userId, user);
        List<StudyInfoResponse> studyInfoList = studyInfoService.selectUserStudyInfoList(userId, cursorIdx, limit);

        // 다음 cursorIdx
        Long nextCursorIdx = null;
        if (!studyInfoList.isEmpty()) {
            nextCursorIdx = studyInfoList.get(studyInfoList.size() - 1).getId();
        }
        return JsonResult.successOf(StudyInfoListAndCursorIdxResponse.builder()
                .studyInfoList(studyInfoList)
                .cursorIdx(nextCursorIdx)
                .build());
    }

    // 정렬된 모든 스터디 조회
    @ApiResponse(responseCode = "200",
            description = "정렬된 모든 스터디 조회 성공",
            content = @Content(schema = @Schema(implementation =
                    AllStudyInfoListAndCursorIdxResponse.class)))
    @GetMapping("/all/sort")
    public JsonResult<?> userStudyInfoListByParameter(@AuthenticationPrincipal User user,
                                                      @RequestParam(name = "limit") Long limit,
                                                      @Min(value = 0, message = "Cursor index cannot be negative")
                                                      @RequestParam(name = "cursorIdx") Long cursorIdx,
                                                      @RequestParam(name = "sortBy") String sortBy
    ) {
        authService.authenticate(user.getId(), user);
        List<AllStudyInfoResponse> studyInfoList = studyInfoService.selectStudyInfoListbyParameter(user.getId(), cursorIdx, limit, sortBy);

        // 다음 cursorIdx
        Long nextCursorIdx = null;
        if (!studyInfoList.isEmpty()) {
            nextCursorIdx = studyInfoList.get(studyInfoList.size() - 1).getId();
        }
        return JsonResult.successOf(AllStudyInfoListAndCursorIdxResponse.builder()
                .studyInfoList(studyInfoList)
                .cursorIdx(nextCursorIdx)
                .build());
    }
}
