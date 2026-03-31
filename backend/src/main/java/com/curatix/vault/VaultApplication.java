package com.curatix.vault;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class VaultApplication {
    public static void main(String[] args) {
        // Load .env variables into System properties for local development
        Dotenv.configure()
                .directory("./")
                .ignoreIfMissing()
                .systemProperties()
                .load();
        
        SpringApplication.run(VaultApplication.class, args);
    }
}
