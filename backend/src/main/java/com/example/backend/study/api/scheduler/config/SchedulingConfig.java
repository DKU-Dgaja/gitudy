package com.example.backend.study.api.scheduler.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
@EnableScheduling
public class SchedulingConfig implements SchedulingConfigurer {
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        TaskScheduler scheduler = taskScheduler();
        taskRegistrar.setTaskScheduler(scheduler);
    }

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(10); // 스레드 풀의 개수 설정
        scheduler.setThreadNamePrefix("Gitudy-scheduler-"); // 스레드 이름 접두사 설정
        scheduler.setRemoveOnCancelPolicy(true); // 취소된 작업의 스레드를 제거
        scheduler.setErrorHandler(t -> {
            // 에러 핸들러 설정
            System.err.println("Error occurred in scheduled task - " + t.getMessage());
        });
        scheduler.initialize();
        return scheduler;
    }
}
