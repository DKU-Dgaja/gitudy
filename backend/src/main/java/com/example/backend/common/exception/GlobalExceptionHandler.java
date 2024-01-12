package com.example.backend.common.exception;

import com.example.backend.common.response.JsonResult;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;


@RestControllerAdvice
public class GlobalExceptionHandler {
    /*
        데이터 바인딩 중 발생하는 에러 BindException 처리
     */
    @ExceptionHandler(BindException.class)
    public JsonResult bindException(BindException e) {
        return JsonResult.failOf(
                e.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                        .collect(Collectors.joining(", "))
        );
    }

    /*
        @Valid 어노테이션을 사용한 DTO의 유효성 검사에서 예외가 발생한 경우
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public JsonResult handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        return JsonResult.failOf(
                e.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                        .collect(Collectors.joining(", "))
        );
    }

    @ExceptionHandler(Exception.class)
    public JsonResult<Exception> exception(Exception e) {
        return JsonResult.failOf(e.getMessage());
    }
}
