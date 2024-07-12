package com.example.backend.common.exception;

import com.example.backend.common.exception.jwt.JwtException;
import com.example.backend.common.response.JsonResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<JsonResult> bindException(BindException e) {
        JsonResult error = JsonResult.failOf(
                e.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                        .collect(Collectors.joining(", "))
        );

        return ResponseEntity.badRequest().body(error);
    }

    /*
        @Valid 어노테이션을 사용한 DTO의 유효성 검사에서 예외가 발생한 경우
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<JsonResult> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        JsonResult error = JsonResult.failOf(
                e.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                        .collect(Collectors.joining(", "))
        );

        return ResponseEntity.badRequest().body(error);
    }

    /*
        JWT 관련 예외 처리
     */
    @ExceptionHandler(com.example.backend.common.exception.jwt.JwtException.class)
    public ResponseEntity<JsonResult> handleJwtException(JwtException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(JsonResult.failOf(e.getMessage()));

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<JsonResult> exception(Exception e) {
        return ResponseEntity.badRequest().body(JsonResult.failOf(e.getMessage()));
    }
}
