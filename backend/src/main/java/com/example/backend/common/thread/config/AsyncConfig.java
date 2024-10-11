package com.example.backend.common.thread.config;

import com.example.backend.common.thread.MyAsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(16); // 기본 스레드 수
        executor.setMaxPoolSize(32); // 최대 스레드 수
        executor.setQueueCapacity(150); // 큐 크기

        executor.setKeepAliveSeconds(60); // 사용되지 않은 스레드는 60초 후 종료
        executor.setAllowCoreThreadTimeOut(true); // 코어 스레드도 타임아웃 가능
        executor.setPrestartAllCoreThreads(true); // 모든 코어 스레드 미리 시작

        executor.initialize();
        return executor;
    }

    // 비동기 메서드에서 발생하는 예외 처리
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new MyAsyncUncaughtExceptionHandler(); // 커스텀 예외 처리 핸들러 등록
    }
}

