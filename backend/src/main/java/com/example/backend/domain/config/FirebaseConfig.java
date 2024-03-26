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
    public FirebaseMessaging firebaseMessaging() {

        FirebaseApp firebaseApp = null;
        try {
            ClassPathResource resource = new ClassPathResource(fcmKeyPath);
            try (InputStream refreshToken = resource.getInputStream()) {
                List<FirebaseApp> firebaseApps = FirebaseApp.getApps();
                if (!firebaseApps.isEmpty()) {
                    for (FirebaseApp app : firebaseApps) {
                        if (FirebaseApp.DEFAULT_APP_NAME.equals(app.getName())) {
                            firebaseApp = app;
                            log.info("Using existing FirebaseApp instance.");
                            break;
                        }
                    }
                }
                if (firebaseApp == null) {
                    FirebaseOptions options = FirebaseOptions.builder()
                            .setCredentials(GoogleCredentials.fromStream(refreshToken))
                            .build();
                    firebaseApp = FirebaseApp.initializeApp(options);
                    log.info("FCM Setting Completed");
                }
            }
        } catch (Exception e) {
            log.info(">>>>>>>>FCM error");
            log.error(">>>>>>FCM error message : " + e.getMessage());
        }
        return FirebaseMessaging.getInstance(firebaseApp);


    }
}