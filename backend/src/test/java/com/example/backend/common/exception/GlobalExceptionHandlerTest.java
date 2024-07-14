package com.example.backend.common.exception;

import com.example.backend.common.exception.jwt.JwtException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
        var result = globalExceptionHandler.bindException(bindException);

        // then
        assertEquals(result.getStatusCode().value(), HttpStatus.BAD_REQUEST.value());
        assertThat(result.getBody().message()).isEqualTo(expectedResponseMessage);

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
        var result = globalExceptionHandler.handleMethodArgumentNotValid(error);

        // then
        assertEquals(result.getStatusCode().value(), HttpStatus.BAD_REQUEST.value());
        assertThat(result.getBody().message()).isEqualTo(expectedResponseMessage);

    }

    @Test
    @DisplayName("JwtException 발생시 GlobalExceptionHandler에서 처리되며 401(UNAUTHORIZED)를 반환한다.")
    void JwtExceptionTest() {
        // given
        JwtException exception = new JwtException(ExceptionMessage.JWT_MALFORMED);

        // when
        var result = globalExceptionHandler.handleJwtException(exception);

        // then
        assertEquals(result.getStatusCode().value(), HttpStatus.UNAUTHORIZED.value());
        assertThat(result.getBody().message()).isEqualTo(ExceptionMessage.JWT_MALFORMED.getText());
    }

    @Test
    @DisplayName("Exception 발생시 GlobalExceptionHandler에서 처리되며 400(BAD_REQUEST)를 반환한다.")
    void ExceptionTest() {
        // given
        Exception exception = new Exception(ExceptionMessage.AUTH_NOT_FOUND.getText());

        // when
        var result = globalExceptionHandler.exception(exception);

        // then
        assertEquals(result.getStatusCode().value(), HttpStatus.BAD_REQUEST.value());
        assertThat(result.getBody().message()).isEqualTo(ExceptionMessage.AUTH_NOT_FOUND.getText());
    }
}