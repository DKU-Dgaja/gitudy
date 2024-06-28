package com.example.backend.external.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.http.MediaType;
import org.springframework.web.service.annotation.HttpExchange;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
 * @HttpExchange를 확장한 커스텀 어노테이션
 * 사용자가 직접 ProxyFactory 로 빈을 등록해줘야하는 번거로움을 해소해준다.
 */
@Target(ElementType.TYPE)               // 어노테이션 범위: 클래스, 인터페이스 등의 타입 선언부
@Retention(RetentionPolicy.RUNTIME)     // 어노테이션 유지 시간: 런타임
@HttpExchange                           // @HttpExchange 확장 커스텀 어노테이션
public @interface ExternalClients {

    // ExternalClientsPostProcessor 클래스가 application.yml에서 환경변수를 못찾을 경우 사용할 baseUrl 디폴트 값 설정
    String baseUrl() default "http://localhost:8080";

    // HttpExchange의 accept 헤더 기본값 설정: APPLICATION_JSON_VALUE
    @AliasFor(annotation = HttpExchange.class, attribute = "accept")
    String accept() default MediaType.APPLICATION_JSON_VALUE;
}
