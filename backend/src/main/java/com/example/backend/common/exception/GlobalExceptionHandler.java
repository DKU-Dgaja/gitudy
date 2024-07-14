package com.example.backend.common.exception;

import com.example.backend.common.exception.jwt.JwtException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;


@RestControllerAdvice
public class GlobalExceptionHandler {
    /*
        데이터 바인딩 중 발생하는 에러 BindException 처리
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> bindException(BindException e) {
        String errorMsg = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ErrorResponse error = ErrorResponse.from(BAD_REQUEST.value(), BAD_REQUEST.getReasonPhrase(), errorMsg);

        return ResponseEntity.badRequest().body(error);
    }

    /*
        @Valid 어노테이션을 사용한 DTO의 유효성 검사에서 예외가 발생한 경우
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        String errorMsg = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ErrorResponse error = ErrorResponse.from(BAD_REQUEST.value(), BAD_REQUEST.getReasonPhrase(), errorMsg);

        return ResponseEntity.badRequest().body(error);
    }

    /*
        JWT 관련 예외 처리
     */
    @ExceptionHandler(com.example.backend.common.exception.jwt.JwtException.class)
    public ResponseEntity<ErrorResponse> handleJwtException(JwtException e) {

        return ResponseEntity.status(UNAUTHORIZED)
                .body(ErrorResponse.from(UNAUTHORIZED.value(), UNAUTHORIZED.getReasonPhrase(), e.getMessage()));

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> exception(Exception e) {
        return ResponseEntity.badRequest()
                .body(ErrorResponse.from(BAD_REQUEST.value(), BAD_REQUEST.getReasonPhrase(), e.getMessage()));
    }
}
