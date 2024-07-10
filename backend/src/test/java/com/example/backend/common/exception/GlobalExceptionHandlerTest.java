package com.example.backend.common.exception;

import com.example.backend.common.exception.jwt.JwtException;
import com.example.backend.common.response.JsonResult;
import com.google.api.Http;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;


import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {
    // @SpringBootTest 사용 안하고 테스트하기 위해 그냥 생성
    private final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

    @Test
    @DisplayName("BindException 발생시 GlobalExceptionHandler에서 처리된다.")
    void bindExceptionTest() {
        // given
        BindException bindException = new BindException(new Object(), "objectName");
        bindException.addError(new FieldError("objectName", "fieldName", "rejectedValue", false, null, null, "error message"));

        String expectedResponseMessage = "fieldName: error message";

        // when
        JsonResult result = globalExceptionHandler.bindException(bindException);

        // then
        assertThat(result.getResCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(result.getResMsg()).isEqualTo(expectedResponseMessage);

    }

    @Test
    @DisplayName("MethodArgumentNotValidException 발생시 GlobalExceptionHandler에서 처리된다.")
    void methodArgumentNotValidExceptionTest() {
        // given
        BindingResult bindingResult = new BindException(new Object(), "objectName");
        bindingResult.addError(new FieldError("objectName", "fieldName", "rejectedValue", false, null, null, "error message"));
        MethodArgumentNotValidException error = new MethodArgumentNotValidException(null, bindingResult);

        String expectedResponseMessage = "fieldName: error message";

        // when
        JsonResult result = globalExceptionHandler.handleMethodArgumentNotValid(error);

        // then
        assertThat(result.getResCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(result.getResMsg()).isEqualTo(expectedResponseMessage);

    }

    @Test
    @DisplayName("JwtException 발생시 GlobalExceptionHandler에서 처리되며 401(UNAUTHORIZED)를 반환한다.")
    void JwtExceptionTest() {
        // given
        JwtException exception = new JwtException(ExceptionMessage.JWT_MALFORMED);

        // when
        JsonResult result = globalExceptionHandler.handleJwtException(exception);

        // then
        assertThat(result.getResCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(result.getResMsg()).isEqualTo(ExceptionMessage.JWT_MALFORMED.getText());
    }

    @Test
    @DisplayName("Exception 발생시 GlobalExceptionHandler에서 처리되며 400(BAD_REQUEST)를 반환한다.")
    void ExceptionTest() {
        // given
        Exception exception = new Exception(ExceptionMessage.AUTH_NOT_FOUND.getText());

        // when
        JsonResult result = globalExceptionHandler.exception(exception);

        // then
        assertThat(result.getResCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(result.getResMsg()).isEqualTo(ExceptionMessage.AUTH_NOT_FOUND.getText());
    }
}