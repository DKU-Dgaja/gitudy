package com.example.backend.study.api.controller.todo;

import com.example.backend.common.response.JsonResult;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.study.api.controller.todo.request.StudyTodoRequest;
import com.example.backend.study.api.controller.todo.request.StudyTodoUpdateRequest;
import com.example.backend.study.api.controller.todo.response.StudyTodoListAndCursorIdxResponse;
import com.example.backend.study.api.controller.todo.response.StudyTodoResponse;
import com.example.backend.study.api.controller.todo.response.StudyTodoStatusResponse;
import com.example.backend.study.api.service.member.StudyMemberService;
import com.example.backend.study.api.service.todo.StudyTodoService;
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
@RequestMapping("/study")
public class StudyTodoController {

    private final StudyTodoService studyTodoService;
    private final StudyMemberService studyMemberService;

    // Todo 등록
    @ApiResponse(responseCode = "200", description = "Todo 등록 성공")
    @PostMapping("/{studyInfoId}/todo")
    public JsonResult<?> registerStudyTodo(@AuthenticationPrincipal User user,
                                           @PathVariable("studyInfoId") Long studyInfoId,
                                           @Valid @RequestBody StudyTodoRequest studyTodoRequest) {

        studyMemberService.isValidateStudyLeader(user, studyInfoId);

        studyTodoService.registerStudyTodo(studyTodoRequest, studyInfoId);

        return JsonResult.successOf("Todo register Success");
    }

    // Todo 수정
    @ApiResponse(responseCode = "200", description = "Todo 수정 성공")
    @PutMapping("/{studyInfoId}/todo/{todoId}")
    public JsonResult<?> updateStudyTodo(@AuthenticationPrincipal User user,
                                         @PathVariable(name = "studyInfoId") Long studyInfoId,
                                         @PathVariable(name = "todoId") Long todoId,
                                         @Valid @RequestBody StudyTodoUpdateRequest studyTodoUpdateRequest) {

        studyMemberService.isValidateStudyLeader(user, studyInfoId);

        studyTodoService.updateStudyTodo(studyTodoUpdateRequest, todoId, studyInfoId);

        return JsonResult.successOf("Todo update Success");
    }

    // Todo 삭제
    @ApiResponse(responseCode = "200", description = "Todo 삭제 성공")
    @DeleteMapping("/{studyInfoId}/todo/{todoId}")
    public JsonResult<?> deleteStudyTodo(@AuthenticationPrincipal User user,
                                         @PathVariable(name = "studyInfoId") Long studyInfoId,
                                         @PathVariable(name = "todoId") Long todoId) {

        studyMemberService.isValidateStudyLeader(user, studyInfoId);

        studyTodoService.deleteStudyTodo(todoId, studyInfoId);

        return JsonResult.successOf("Todo delete Success");
    }


    // Todo 전체조회
    @ApiResponse(responseCode = "200", description = "Todo 전체조회 성공", content = @Content(schema = @Schema(implementation = StudyTodoListAndCursorIdxResponse.class)))
    @GetMapping("/{studyInfoId}/todo")
    public JsonResult<?> readStudyTodoList(@AuthenticationPrincipal User user,
                                       @PathVariable(name = "studyInfoId") Long studyInfoId,
                                       @Min(value = 0, message = "Cursor index cannot be negative") @RequestParam(name = "cursorIdx", required = false) Long cursorIdx,
                                       @Min(value = 1, message = "Limit cannot be less than 1") @RequestParam(name = "limit", defaultValue = "3") Long limit) {

        // 스터디 멤버인지 검증
        studyMemberService.isValidateStudyMember(user, studyInfoId);

        return JsonResult.successOf(studyTodoService.readStudyTodoList(studyInfoId, cursorIdx, limit));
    }


    // Todo 단일조회
    @ApiResponse(responseCode = "200", description = "Todo 조회 성공", content = @Content(schema = @Schema(implementation = StudyTodoResponse.class)))
    @GetMapping("/{studyInfoId}/todo/{todoId}")
    public JsonResult<?> readStudyTodo(@AuthenticationPrincipal User user,
                                       @PathVariable(name = "studyInfoId") Long studyInfoId,
                                       @PathVariable(name = "todoId") Long todoId) {

        studyMemberService.isValidateStudyMember(user, studyInfoId);

        return JsonResult.successOf(studyTodoService.readStudyTodo(studyInfoId, todoId));
    }

    // 스터디원들의 Todo 완료여부 조회
    @ApiResponse(responseCode = "200", description = "Todo 완료조회 성공", content = @Content(schema = @Schema(implementation = StudyTodoStatusResponse.class)))
    @GetMapping("/{studyInfoId}/todo/{todoId}/status")
    public JsonResult<?> readStudyTodoStatus(@AuthenticationPrincipal User user,
                                             @PathVariable(name = "studyInfoId") Long studyInfoId,
                                             @PathVariable(name = "todoId") Long todoId) {

        // 스터디 멤버인지 검증
        studyMemberService.isValidateStudyMember(user, studyInfoId);

        return JsonResult.successOf(studyTodoService.readStudyTodoStatus(studyInfoId, todoId));
    }
}