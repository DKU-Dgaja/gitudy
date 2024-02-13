package com.example.backend.study.api.controller.todo;

import com.example.backend.common.response.JsonResult;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.study.api.controller.todo.request.StudyTodoRequest;
import com.example.backend.study.api.controller.todo.request.StudyTodoUpdateRequest;
import com.example.backend.study.api.controller.todo.response.StudyTodoResponse;
import com.example.backend.study.api.service.todo.StudyTodoService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/study")
public class StudyTodoController {

    private final StudyTodoService studyTodoService;

    // Todo 등록
    @ApiResponse(responseCode = "200", description = "Todo 등록 성공")
    @PostMapping("/{studyInfoId}/todo/register")
    public JsonResult<?> registerStudyTodo(@AuthenticationPrincipal User user,
                                           @PathVariable("studyInfoId") Long studyInfoId,
                                           @Valid @RequestBody StudyTodoRequest studyTodoRequest) {


        studyTodoService.registerStudyTodo(studyTodoRequest, studyInfoId, user);


        return JsonResult.successOf("Todo register Success");
    }


    // Todo 조회
    @ApiResponse(responseCode = "200", description = "Todo 수정 성공", content = @Content(schema = @Schema(implementation = StudyTodoResponse.class)))
    @GetMapping("/{studyInfoId}/todo/read")
    public JsonResult<?> readStudyTodo(@AuthenticationPrincipal User user,
                                       @PathVariable(name = "studyInfoId") Long studyInfoId) {


        List<StudyTodoResponse> studyTodoResponses = studyTodoService.readStudyTodo(studyInfoId);


        return JsonResult.successOf(studyTodoResponses);
    }

    // Todo 수정
    @ApiResponse(responseCode = "200", description = "Todo 수정 성공")
    @PutMapping("/{todoId}/todo/update")
    public JsonResult<?> updateStudyTodo(@AuthenticationPrincipal User user,
                                         @PathVariable(name = "todoId") Long todoId,
                                         @Valid @RequestBody StudyTodoUpdateRequest request) {


        studyTodoService.updateStudyTodo(todoId, request, user);

        return JsonResult.successOf("Todo update Success");
    }


    // Todo 삭제
    @ApiResponse(responseCode = "200", description = "Todo 삭제 성공")
    @DeleteMapping("/{studyInfoId}/{todoId}/todo/delete")
    public JsonResult<?> deleteStudyTodo(@AuthenticationPrincipal User user,
                                         @PathVariable(name = "studyInfoId") Long studyInfoId,
                                         @PathVariable(name = "todoId") Long todoId) {

        studyTodoService.deleteStudyTodo(studyInfoId, todoId, user);

        return JsonResult.successOf("Todo delete Success");
    }

}
