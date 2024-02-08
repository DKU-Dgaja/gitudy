package com.example.backend.study.api.controller.todo;

import com.example.backend.auth.api.service.auth.AuthService;
import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.todo.TodoException;
import com.example.backend.common.response.JsonResult;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.constant.UserPlatformType;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.todo.info.StudyTodo;
import com.example.backend.domain.define.study.todo.mapping.StudyTodoMapping;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/study")
public class StudyTodoController {

    private final AuthService authService;
    private final StudyTodoService studyTodoService;
    private final UserRepository userRepository;

    // Todo 등록
    @ApiResponse(responseCode = "200", description = "Todo 등록 성공")
    @PostMapping("/{studyInfoId}/todo/register")
    public JsonResult<?> registerStudyTodo(@AuthenticationPrincipal User userPrincipal,
                                           @PathVariable("studyInfoId") Long studyInfoId,
                                           @Valid @RequestBody StudyTodoRequest studyTodoRequest) {

        authService.authenticate(userPrincipal);

        // platformId와 platformType을 이용하여 User 객체 조회
        User user = userRepository.findByPlatformIdAndPlatformType(userPrincipal.getPlatformId(), userPrincipal.getPlatformType()).orElseThrow(() -> {
            log.warn(">>>> {},{} : {} <<<<", userPrincipal.getPlatformId(), userPrincipal.getPlatformType(), ExceptionMessage.USER_NOT_FOUND);
            return new TodoException(ExceptionMessage.USER_NOT_FOUND);
        });


        StudyTodo studyTodo = studyTodoRequest.registerStudyTodo();

        studyTodoService.registerStudyTodo(studyTodo, studyInfoId, user.getId());


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
    public JsonResult<?> updateStudyTodo(@AuthenticationPrincipal User userPrincipal,
                                         @PathVariable(name = "todoId") Long todoId,
                                         @Valid @RequestBody StudyTodoUpdateRequest request) {

        authService.authenticate(userPrincipal);

        // platformId와 platformType을 이용하여 User 객체 조회
        User user = userRepository.findByPlatformIdAndPlatformType(userPrincipal.getPlatformId(), userPrincipal.getPlatformType()).orElseThrow(() -> {
            log.warn(">>>> {},{} : {} <<<<", userPrincipal.getPlatformId(), userPrincipal.getPlatformType(), ExceptionMessage.USER_NOT_FOUND);
            return new TodoException(ExceptionMessage.USER_NOT_FOUND);
        });

        studyTodoService.updateStudyTodo(todoId, request, user.getId());

        return JsonResult.successOf("Todo update Success");
    }


    // Todo 삭제
    @ApiResponse(responseCode = "200", description = "Todo 삭제 성공")
    @DeleteMapping("/{studyInfoId}/{todoId}/todo/delete")
    public JsonResult<?> deleteStudyTodo(@AuthenticationPrincipal User userPrincipal,
                                         @PathVariable(name = "studyInfoId") Long studyInfoId,
                                         @PathVariable(name = "todoId") Long todoId) {
        authService.authenticate(userPrincipal);

        // platformId와 platformType을 이용하여 User 객체 조회
        User user = userRepository.findByPlatformIdAndPlatformType(userPrincipal.getPlatformId(), userPrincipal.getPlatformType()).orElseThrow(() -> {
            log.warn(">>>> {},{} : {} <<<<", userPrincipal.getPlatformId(), userPrincipal.getPlatformType(), ExceptionMessage.USER_NOT_FOUND);
            return new TodoException(ExceptionMessage.USER_NOT_FOUND);
        });

        studyTodoService.deleteStudyTodo(studyInfoId, todoId, user.getId());
        return JsonResult.successOf("Todo delete Success");
    }

}
