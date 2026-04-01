package com.curatix.vault.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    @GetMapping
    public Map<String, String> healthCheck() {
        return Map.of(
            "status", "UP",
            "message", "CuratiX Vault Backend is live and breathing! ◈",
            "timestamp", String.valueOf(System.currentTimeMillis())
        );
    }
}
