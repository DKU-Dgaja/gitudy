package com.example.backend.external.config;

import com.example.backend.external.annotation.ExternalClients;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

/*
 * BeanFactoryPostProcessor
 * Application Context가 생성될 때 실행되는 인터페이스
 * 빈의 설정을 변경하거나 추가적인 빈을 동적으로 등록

 * EnvironmentAware
 * Context 생성 및 초기화 시 Environment(application.yml)를 주입받음
 * 빈 내부에서 Environment(application.yml)를 사용할 수 있음
 */
@Slf4j
@Component
public class ExternalClientsPostProcessor implements BeanFactoryPostProcessor, EnvironmentAware {

    private Environment environment;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        /*
         * Reflection 라이브러리
         * 프로그램 실행 중에 클래스, 메서드, 필드 등의 정보를 조사할 때 사용
         * 적용할 external 패키지 지정
         */
        Reflections ref = new Reflections("com.example.backend.external");

        // @ExternalClients 어노테이션이 적용된 클래스 조사
        for (Class<?> clazz : ref.getTypesAnnotatedWith(ExternalClients.class)) {
            ExternalClients annotation = clazz.getAnnotation(ExternalClients.class);

            // application.yml에서 @ExternalClients 어노테이션에 정의된 baseUrl 프로퍼티 획득
            String baseUrl = environment.getProperty(annotation.baseUrl());

            // baseUrl에 해당하는 환경변수를 못찾았을 경우 어노테이션에 설정된 디폴트 값 사용
            baseUrl = (baseUrl != null) ? baseUrl : annotation.baseUrl();

            // baseUrl을 이용해 WebCllient 생성
            WebClient webClient = WebClient.builder()
                    .baseUrl(baseUrl)
                    .build();

            // ProxyFactory & 클라이언트 프록시 생성 후 빈으로 등록
            HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(WebClientAdapter.create(webClient)).build();
            Object externalClientsBean = factory.createClient(clazz);
            beanFactory.registerSingleton(clazz.getSimpleName(), externalClientsBean);

            log.info(">>>> [ Success External Clients : {}, baseUrl : {} ] <<<<", clazz.getSimpleName(), baseUrl);
        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
