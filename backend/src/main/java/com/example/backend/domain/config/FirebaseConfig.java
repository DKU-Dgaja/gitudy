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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Configuration
public class FirebaseConfig {

    @Value("${firebase.key-path}")
    private String fcmKeyPath;

   /* @Bean
    public FirebaseMessaging firebaseMessaging() throws IOException {
        ClassPathResource resource = new ClassPathResource(fcmKeyPath);
        InputStream refreshToken = resource.getInputStream();

        FirebaseApp firebaseApp = null;
        List<FirebaseApp> firebaseAppList = FirebaseApp.getApps();

        if (firebaseAppList != null && !firebaseAppList.isEmpty()) {
            for(FirebaseApp app: firebaseAppList) {
                if(app.getName().equals(FirebaseApp.DEFAULT_APP_NAME)) {
                    firebaseApp = app;
                }
            }
        }

        if(firebaseApp == null) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(refreshToken))
                    .build();
            firebaseApp = FirebaseApp.initializeApp(options);
            log.info("FCM Setting Complete");
        }

        return FirebaseMessaging.getInstance(firebaseApp);
    }*/

   /* @PostConstruct
    public void initialize() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                InputStream credentials = new ClassPathResource(fcmKeyPath).getInputStream();
                FirebaseOptions options = new FirebaseOptions.Builder()
                        .setCredentials(GoogleCredentials.fromStream(credentials))
                        .build();
                FirebaseApp.initializeApp(options);
                log.info("FCM Setting Completed");
            }
        } catch (IOException e) {
            log.error("FCM error message : " + e.getMessage());
        }
    }

    @Bean
    public FirebaseMessaging firebaseMessaging() {
        return FirebaseMessaging.getInstance();
    }*/

    @Bean
    public FirebaseMessaging firebaseMessaging() {
        if (FirebaseApp.getApps().isEmpty()) { // FirebaseApp이 초기화되지 않았는지 확인
            try {
                InputStream credentials = new ClassPathResource(fcmKeyPath).getInputStream();
                String text = new String(credentials.readAllBytes(), StandardCharsets.UTF_8);

                FirebaseOptions options = new FirebaseOptions.Builder()
                        .setCredentials(GoogleCredentials.fromStream(new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8))))
                        .build();

                if (FirebaseApp.getApps().isEmpty()) {
                    FirebaseApp.initializeApp(options);
                }
            } catch (IOException e) {
                log.error("FCM Initialization error: " + e.getMessage());
                throw new IllegalStateException("Failed to initialize FirebaseApp", e);
            }
        }
        log.info("FCM Setting Completed");
        return FirebaseMessaging.getInstance(); // 이제 안전하게 FirebaseMessaging 인스턴스를 가져올 수 있음
    }

}