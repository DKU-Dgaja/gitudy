package com.example.backend.common.thread;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import java.lang.reflect.Method;

public class MyAsyncUncaughtExceptionHandler implements AsyncUncaughtExceptionHandler {
    @Override
    public void handleUncaughtException(Throwable ex, Method method, Object... params) {
        // 예외 처리 로직 구현
        System.err.println("Exception message: " + ex.getMessage());
        System.err.println("Method name: " + method.getName());

        for (Object param : params) {
            System.err.println("Parameter value: " + param);
        }

        // 로그 저장 또는 알림..

    }
}

