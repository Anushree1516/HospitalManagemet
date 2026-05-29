package com.bloodbank.bloodbank.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseConfig.class);

    @Value("${firebase.config.path:firebase-service-account.json}")
    private String firebaseConfigPath;

    @PostConstruct
    public void initializeFirebase() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                // Try loading from classpath (resources folder)
                InputStream serviceAccount;
                try {
                    serviceAccount = new ClassPathResource("firebase-service-account.json")
                            .getInputStream();
                } catch (Exception e) {
                    logger.warn("Firebase config file not found. Push notifications disabled.");
                    logger.warn("Add firebase-service-account.json to src/main/resources/");
                    return;
                }

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

                FirebaseApp.initializeApp(options);
                logger.info("✅ Firebase initialized successfully");
            }
        } catch (IOException e) {
            logger.error("❌ Firebase initialization failed: {}", e.getMessage());
        }
    }
}
