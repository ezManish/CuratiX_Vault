package com.curatix.vault.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import jakarta.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

@Configuration
public class FirebaseConfig {

    @Value("${firebase.credentials-path}")
    private String credentialsPath;

    @Value("${FIREBASE_ADMIN_JSON_BASE64:}")
    private String credentialsBase64;

    private final ResourceLoader resourceLoader;

    public FirebaseConfig(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    public void initFirebase() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {
            InputStream serviceAccount;

            if (credentialsBase64 != null && !credentialsBase64.trim().isEmpty()) {
                // Use Base64 encoded JSON from environment variable
                try {
                    byte[] decoded = Base64.getDecoder().decode(credentialsBase64.trim());
                    serviceAccount = new ByteArrayInputStream(decoded);
                } catch (IllegalArgumentException e) {
                    throw new IOException("Invalid Base64 string in FIREBASE_ADMIN_JSON_BASE64 env var", e);
                }
            } else {
                // Fall back to local file path
                Resource resource = resourceLoader.getResource(credentialsPath);
                serviceAccount = resource.getInputStream();
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp.initializeApp(options);
        }
    }
}
