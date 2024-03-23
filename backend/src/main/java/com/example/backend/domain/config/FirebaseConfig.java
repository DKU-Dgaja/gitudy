package com.example.backend.domain.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
@Configuration
public class FirebaseConfig {

    @Value("${firebase.key-path}")
    private String fcmKeyPath;


    @PostConstruct
    public void getFcmCredential() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                GoogleCredentials credentials = GoogleCredentials
                        .fromStream(new ClassPathResource(fcmKeyPath).getInputStream());

                FirebaseOptions options = new FirebaseOptions.Builder()
                        .setCredentials(credentials)
                        .build();

                FirebaseApp.initializeApp(options);


                log.info("Fcm Setting Completed");
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}