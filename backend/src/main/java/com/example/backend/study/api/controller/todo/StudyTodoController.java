package com.example.backend.study.api.controller.todo;

import com.example.backend.common.response.JsonResult;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.study.api.controller.todo.request.StudyTodoRequest;
import com.example.backend.study.api.service.member.StudyMemberService;
import com.example.backend.study.api.service.todo.StudyTodoService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
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
}