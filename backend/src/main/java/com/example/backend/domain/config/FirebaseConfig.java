package com.example.backend.domain.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
@Configuration
public class FirebaseConfig {

    @Value("${firebase.key-path}")
    private String fcmKeyPath;


    @Bean
    public FirebaseMessaging firebaseMessaging() throws IOException {
        ClassPathResource resource = new ClassPathResource(fcmKeyPath);
        InputStream refreshToken = resource.getInputStream();

        FirebaseApp firebaseApp = null;
        List<FirebaseApp> firebaseAppList = FirebaseApp.getApps();

        // FirebaseApp 인스턴스 중 DEFAULT_APP_NAME을 가진 인스턴스가 이미 존재하는지 확인
        if (!firebaseAppList.isEmpty()) {
            for (FirebaseApp app : firebaseAppList) {
                if (FirebaseApp.DEFAULT_APP_NAME.equals(app.getName())) {
                    firebaseApp = app;
                    break;
                }
            }
        }

        // 기존 인스턴스가 없다면 새 인스턴스 생성
        if(firebaseApp == null) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(refreshToken))
                    .build();
            firebaseApp = FirebaseApp.initializeApp(options);
            log.info("FCM Setting Completed");
        }

        return FirebaseMessaging.getInstance(firebaseApp);
    }

}