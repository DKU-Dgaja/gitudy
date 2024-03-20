package com.example.backend.auth.config.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Configuration
public class FirebaseConfig {

    @Value("${firebase.key-path}")
    String fcmKeyPath;

    @PostConstruct
    public void getFcmCredential() {
        try {
            InputStream refreshToken = new ClassPathResource(fcmKeyPath).getInputStream();

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(refreshToken)).build();

            FirebaseApp.initializeApp(options);
            log.info("Fcm Setting Completed");

        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}