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
    public FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp){
        return FirebaseMessaging.getInstance(firebaseApp);
    }

    @PostConstruct
    public void getFcmCredential() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                InputStream credentials = new ClassPathResource(fcmKeyPath).getInputStream();

                FirebaseOptions options = new FirebaseOptions.Builder()
                        .setCredentials(GoogleCredentials.fromStream(credentials))
                        .build();

                FirebaseApp.initializeApp(options);
                log.info("Fcm Setting Completed");
            }
        } catch (IOException e) {
            log.info(">>>>>>>>FCM error");
            log.error(">>>>>>FCM error message : " + e.getMessage());
        }
    }
}