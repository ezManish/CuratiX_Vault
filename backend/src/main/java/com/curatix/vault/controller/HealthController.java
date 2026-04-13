package com.curatix.vault.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/health")
@Tag(name = "Health", description = "System liveness and monitoring endpoints")
public class HealthController {

    @Operation(summary = "Health Check", description = "Returns the current status of the backend server. Used for monitoring and keeping free-tier instances alive.")
    @GetMapping
    public Map<String, String> healthCheck() {
        return Map.of(
            "status", "UP",
            "message", "CuratiX Vault Backend is live and breathing! ◈",
            "timestamp", String.valueOf(System.currentTimeMillis())
        );
    }
}
