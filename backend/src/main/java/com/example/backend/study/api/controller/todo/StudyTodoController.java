package com.example.backend.study.api.controller.todo;

import com.example.backend.domain.define.account.user.User;
import com.example.backend.study.api.controller.todo.request.StudyTodoRequest;
import com.example.backend.study.api.controller.todo.request.StudyTodoUpdateRequest;
import com.example.backend.study.api.controller.todo.response.StudyTodoListAndCursorIdxResponse;
import com.example.backend.study.api.controller.todo.response.StudyTodoProgressResponse;
import com.example.backend.study.api.controller.todo.response.StudyTodoResponse;
import com.example.backend.study.api.controller.todo.response.StudyTodoStatusResponse;
import com.example.backend.study.api.service.commit.response.CommitInfoResponse;
import com.example.backend.study.api.service.member.StudyMemberService;
import com.example.backend.study.api.service.todo.StudyTodoService;
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

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/study")
public class StudyTodoController {

    private final StudyTodoService studyTodoService;
    private final StudyMemberService studyMemberService;

    // Todo 등록
    @ApiResponse(responseCode = "200", description = "Todo 등록 성공")
    @PostMapping("/{studyInfoId}/todo")
    public ResponseEntity<Void> registerStudyTodo(@AuthenticationPrincipal User user,
                                                  @PathVariable("studyInfoId") Long studyInfoId,
                                                  @Valid @RequestBody StudyTodoRequest studyTodoRequest) {

        studyMemberService.isValidateStudyLeader(user, studyInfoId);

        studyTodoService.registerStudyTodo(studyTodoRequest, studyInfoId);

        return ResponseEntity.ok().build();
    }

    // Todo 수정
    @ApiResponse(responseCode = "200", description = "Todo 수정 성공")
    @PutMapping("/{studyInfoId}/todo/{todoId}")
    public ResponseEntity<Void> updateStudyTodo(@AuthenticationPrincipal User user,
                                                @PathVariable(name = "studyInfoId") Long studyInfoId,
                                                @PathVariable(name = "todoId") Long todoId,
                                                @Valid @RequestBody StudyTodoUpdateRequest studyTodoUpdateRequest) {

        studyMemberService.isValidateStudyLeader(user, studyInfoId);

        studyTodoService.updateStudyTodo(studyTodoUpdateRequest, todoId, studyInfoId);

        return ResponseEntity.ok().build();
    }

    // Todo 삭제
    @ApiResponse(responseCode = "200", description = "Todo 삭제 성공")
    @DeleteMapping("/{studyInfoId}/todo/{todoId}")
    public ResponseEntity<Void> deleteStudyTodo(@AuthenticationPrincipal User user,
                                                @PathVariable(name = "studyInfoId") Long studyInfoId,
                                                @PathVariable(name = "todoId") Long todoId) {

        studyMemberService.isValidateStudyLeader(user, studyInfoId);

        studyTodoService.deleteStudyTodo(todoId, studyInfoId);

        return ResponseEntity.ok().build();
    }


    // Todo 전체조회 (커밋 리스트 포함)
    @ApiResponse(responseCode = "200", description = "Todo 전체조회 성공 (커밋 리스트 포함)", content = @Content(schema = @Schema(implementation = StudyTodoListAndCursorIdxResponse.class)))
    @GetMapping("/{studyInfoId}/todo")
    public ResponseEntity<StudyTodoListAndCursorIdxResponse> readStudyTodoList(@AuthenticationPrincipal User user,
                                                                               @PathVariable(name = "studyInfoId") Long studyInfoId,
                                                                               @Min(value = 0, message = "Cursor index cannot be negative") @RequestParam(name = "cursorIdx", required = false) Long cursorIdx,
                                                                               @Min(value = 1, message = "Limit cannot be less than 1") @RequestParam(name = "limit", defaultValue = "3") Long limit,
                                                                               @RequestParam(name = "fetchFlag", defaultValue = "false") boolean fetchFlag) {
        // 스터디 멤버인지 검증
        studyMemberService.isValidateStudyMember(user, studyInfoId);

        return ResponseEntity.ok().body(studyTodoService.readStudyTodoList(studyInfoId, cursorIdx, limit));
    }

    // Todo 단일조회
    @ApiResponse(responseCode = "200", description = "Todo 조회 성공", content = @Content(schema = @Schema(implementation = StudyTodoResponse.class)))
    @GetMapping("/{studyInfoId}/todo/{todoId}")
    public ResponseEntity<StudyTodoResponse> readStudyTodo(@AuthenticationPrincipal User user,
                                                           @PathVariable(name = "studyInfoId") Long studyInfoId,
                                                           @PathVariable(name = "todoId") Long todoId) {

        studyMemberService.isValidateStudyMember(user, studyInfoId);

        return ResponseEntity.ok().body(studyTodoService.readStudyTodo(studyInfoId, todoId));
    }

    // 스터디원들의 Todo 완료여부 조회
    @ApiResponse(responseCode = "200", description = "Todo 완료조회 성공", content = @Content(schema = @Schema(implementation = StudyTodoStatusResponse.class)))
    @GetMapping("/{studyInfoId}/todo/{todoId}/status")
    public ResponseEntity<List<StudyTodoStatusResponse>> readStudyTodoStatus(@AuthenticationPrincipal User user,
                                                                             @PathVariable(name = "studyInfoId") Long studyInfoId,
                                                                             @PathVariable(name = "todoId") Long todoId) {

        // 스터디 멤버인지 검증
        studyMemberService.isValidateStudyMember(user, studyInfoId);

        return ResponseEntity.ok().body(studyTodoService.readStudyTodoStatus(studyInfoId, todoId));
    }

    // 가장 마감일이 빠른 Todo의 진행률 조회
    @ApiResponse(responseCode = "200", description = "마감일이 가장 가까운 Todo의 진행률 조회", content = @Content(schema = @Schema(implementation = StudyTodoProgressResponse.class)))
    @GetMapping("/{studyInfoId}/todo/progress")
    public ResponseEntity<StudyTodoProgressResponse> readStudyTodoProgress(@AuthenticationPrincipal User user,
                                                                           @PathVariable(name = "studyInfoId") Long studyInfoId) {

        // 스터디 멤버인지 검증
        studyMemberService.isValidateStudyMember(user, studyInfoId);

        return ResponseEntity.ok().body(studyTodoService.readStudyTodoProgress(studyInfoId));
    }

    // Todo별 커밋 리스트 조회
    @ApiResponse(responseCode = "200", description = "Todo 별 커밋 리스트 조회", content = @Content(schema = @Schema(implementation = CommitInfoResponse.class)))
    @GetMapping("/{studyInfoId}/todo/{todoId}/commits")
    public ResponseEntity<List<CommitInfoResponse>> selectTodoCommits(@AuthenticationPrincipal User user,
                                                                      @PathVariable(name = "studyInfoId") Long studyInfoId,
                                                                      @PathVariable(name = "todoId") Long todoId) {
        studyMemberService.isValidateStudyMember(user, studyInfoId);

        return ResponseEntity.ok().body(studyTodoService.selectTodoCommits(todoId));
    }

}