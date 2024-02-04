package com.example.backend.study.api.controller.info;

import com.example.backend.auth.api.service.auth.AuthService;
import com.example.backend.study.api.service.info.StudyInfoService;
import com.example.backend.common.response.JsonResult;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.study.info.StudyInfo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/studyinfo")
public class StudyInfoController {

    private final StudyInfoService studyInfoService;
    private final AuthService authService;

    // 스터디 등록
    @PostMapping("/register")
    public JsonResult<?> registerStudy(@AuthenticationPrincipal User user,
                                       @Valid @RequestBody StudyInfo studyInfoRequest) {
        authService.authenticate(user);
        StudyInfo registeredStudy = studyInfoService.registerStudy(studyInfoRequest);
        return JsonResult.successOf("Study Register Success.");
    }

    // 한개의 스터디 상세정보 조회
    @GetMapping("/{studyInfoId}")
    public JsonResult<?> getStudyInfo(@AuthenticationPrincipal User user,
                                      @PathVariable(name = "studyInfoId") Long studyInfoId) {
        authService.authenticate(user);
        Optional<StudyInfo> studyInfo = studyInfoService.selectStudyInfo(studyInfoId);
        return JsonResult.successOf(studyInfo);
    }

    // 모든 스터디 상세정보 조회
    @GetMapping("/all")
    public JsonResult<?> getAllStudyInfo(@AuthenticationPrincipal User user) {
        authService.authenticate(user);
        List<StudyInfo> studyInfoList=studyInfoService.selectStudyInfoList();
        return JsonResult.successOf(studyInfoList);
    }

    // 스터디 삭제
    @DeleteMapping("/{studyInfoId}")
    public JsonResult<?> deleteStudy(@AuthenticationPrincipal User user,
                                     @PathVariable(name = "studyInfoId") Long studyInfoId) {
        authService.authenticate(user);
        studyInfoService.deleteStudy(studyInfoId);
        return JsonResult.successOf("Study deleted successfully");
    }
}
