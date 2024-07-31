package com.example.backend.study.api.controller.info;

import com.example.backend.auth.api.controller.auth.response.UserInfoResponse;
import com.example.backend.auth.api.service.auth.AuthService;
import com.example.backend.auth.api.service.rank.RankingService;
import com.example.backend.auth.api.service.rank.response.StudyRankingResponse;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.study.api.controller.info.request.RepoNameCheckRequest;
import com.example.backend.study.api.controller.info.request.StudyInfoRegisterRequest;
import com.example.backend.study.api.controller.info.request.StudyInfoUpdateRequest;
import com.example.backend.study.api.controller.info.response.StudyInfoCountResponse;
import com.example.backend.study.api.controller.info.response.StudyInfoDetailResponse;
import com.example.backend.study.api.controller.info.response.StudyInfoListAndCursorIdxResponse;
import com.example.backend.study.api.controller.info.response.UpdateStudyInfoPageResponse;
import com.example.backend.study.api.service.info.StudyInfoService;
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
public class StudyInfoController {
    private final StudyInfoService studyInfoService;
    private final AuthService authService;
    private final StudyMemberService studyMemberService;
    private final RankingService rankingService;

    @ApiResponse(responseCode = "200", description = "스터디 등록 성공")
    @PostMapping("/")
    public ResponseEntity<?> registerStudy(@AuthenticationPrincipal User user,
                                           @Valid @RequestBody StudyInfoRegisterRequest studyInfoRequest) {
        UserInfoResponse findUser = authService.findUserInfo(user);
        studyInfoService.registerStudy(studyInfoRequest, findUser);
        return ResponseEntity.ok().build();
    }

    @ApiResponse(responseCode = "200", description = "스터디 레포지토리 이름 검증 성공")
    @PostMapping("/check-name")
    public ResponseEntity<Void> repoNameValidCheck(@AuthenticationPrincipal User user,
                                                   @Valid @RequestBody RepoNameCheckRequest request) {

        UserInfoResponse userInfo = authService.findUserInfo(user);
        studyInfoService.checkDuplicateRepoName(userInfo, request.name());

        return ResponseEntity.ok().build();
    }

    @ApiResponse(responseCode = "200", description = "스터디 삭제 성공")
    @DeleteMapping("/{studyInfoId}")
    public ResponseEntity<Void> deleteStudy(@AuthenticationPrincipal User user,
                                            @PathVariable(name = "studyInfoId") Long studyInfoId) {
        // 유저가 스터디 장이 아닐 경우 예외 발생
        studyMemberService.isValidateStudyLeader(user, studyInfoId);
        studyInfoService.deleteStudy(studyInfoId);

        return ResponseEntity.ok().build();
    }

    @ApiResponse(responseCode = "200", description = "스터디 정보 수정 성공")
    @PatchMapping("/{studyInfoId}")
    public ResponseEntity<Void> updateStudyInfo(@AuthenticationPrincipal User user,
                                                @PathVariable(name = "studyInfoId") Long studyInfoId,
                                                @Valid @RequestBody StudyInfoUpdateRequest studyInfoUpdateRequest) {

        // 리더인지 확인
        studyMemberService.isValidateStudyLeader(user, studyInfoId);

        studyInfoService.updateStudyInfo(studyInfoUpdateRequest, studyInfoId);

        return ResponseEntity.ok().build();
    }

    @ApiResponse(responseCode = "200", description = "스터디 정보 수정 페이지 요청 성공")
    @GetMapping("/{studyInfoId}/update")
    public ResponseEntity<UpdateStudyInfoPageResponse> updateStudyInfoPage(@AuthenticationPrincipal User user,
                                                                           @PathVariable(name = "studyInfoId") Long studyInfoId) {
        // 리더인지 확인
        studyMemberService.isValidateStudyLeader(user, studyInfoId);
        return ResponseEntity.ok().body(studyInfoService.updateStudyInfoPage(studyInfoId));
    }

    // 스터디 조회
    @ApiResponse(responseCode = "200", description = "스터디 조회 성공", content = @Content(schema = @Schema(implementation =
            StudyInfoListAndCursorIdxResponse.class)))
    @GetMapping("/")
    public ResponseEntity<StudyInfoListAndCursorIdxResponse> myStudyInfoListByParameter(@AuthenticationPrincipal User user,
                                                                                        @Min(value = 0, message = "Cursor index cannot be negative")
                                                                                        @RequestParam(name = "cursorIdx", required = false) Long cursorIdx,
                                                                                        @RequestParam(name = "limit", defaultValue = "20") Long limit,
                                                                                        @RequestParam(name = "sortBy", defaultValue = "createdDateTime") String sortBy,
                                                                                        @RequestParam(name = "myStudy", defaultValue = "false") boolean myStudy
    ) {
        UserInfoResponse findUser = authService.findUserInfo(user);
        return ResponseEntity.ok().body(studyInfoService.selectStudyInfoList(findUser.getUserId(), cursorIdx, limit, sortBy, myStudy));
    }

    // 스터디 상세정보 조회
    @ApiResponse(responseCode = "200", description = "스터디 상세정보 조회 성공", content = @Content(schema = @Schema(implementation =
            StudyInfoDetailResponse.class)))
    @GetMapping("/{studyInfoId}")
    public ResponseEntity<StudyInfoDetailResponse> getStudyInfo(@AuthenticationPrincipal User user,
                                                                @PathVariable(name = "studyInfoId") Long studyInfoId) {
        UserInfoResponse userInfoResponse = authService.findUserInfo(user);
        return ResponseEntity.ok().body(studyInfoService.selectStudyInfoDetail(studyInfoId, userInfoResponse.getUserId()));
    }

    // 마이/전체스터디 개수 조회
    @ApiResponse(responseCode = "200", description = " 마이/전체스터디 개수 조회 성공", content = @Content(schema = @Schema(implementation =
            StudyInfoCountResponse.class)))
    @GetMapping("/count")
    public ResponseEntity<StudyInfoCountResponse> getStudyInfoCount(@AuthenticationPrincipal User user,
                                                                    @RequestParam(name = "myStudy", defaultValue = "false") boolean myStudy) {
        UserInfoResponse findUser = authService.findUserInfo(user);
        return ResponseEntity.ok().body(studyInfoService.getStudyInfoCount(findUser.getUserId(), myStudy));
    }

    @ApiResponse(responseCode = "200", description = "특정스터디 랭킹 요청 성공", content = @Content(schema = @Schema(implementation =
            StudyRankingResponse.class)))
    @GetMapping("/rank/{studyInfoId}")
    public ResponseEntity<StudyRankingResponse> studyRanking(@AuthenticationPrincipal User user,
                                                             @PathVariable(name = "studyInfoId") Long studyInfoId) {

        authService.findUserInfo(user);
        // rankingService 단에서 스터디 조회하니 순환 의존성 문제가 발생하여 컨트롤러에서 주입
        StudyRankingResponse response = rankingService.getStudyRankings(studyInfoService.findStudyInfoByIdOrThrowException(studyInfoId));

        return ResponseEntity.ok().body(response);
    }
}