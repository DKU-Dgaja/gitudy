package com.example.backend.common.exception;

import com.example.backend.common.response.JsonResult;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;


@RestControllerAdvice
public class GlobalExceptionHandler {
    /*
        데이터 바인딩 중 발생하는 에러 BindException
        * 각 필드에 발생한 에러 매핑
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

    @ExceptionHandler(Exception.class)
    public JsonResult<Exception> exception(Exception e) {
        return JsonResult.failOf(e.getMessage());
    }
}
