package com.curatix.vault.controller;

import com.curatix.vault.entity.UserEntity;
import com.curatix.vault.security.FirebasePrincipal;
import com.curatix.vault.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for synchronizing Firebase users with the local database")
public class AuthController {

    private final UserService userService;

    /**
     * POST /api/auth/sync
     * Called on every login. Upserts Firebase user into MySQL.
     * Returns the user record (id, email, displayName, photoUrl).
     */
    @Operation(summary = "Synchronize User Data", description = "Upserts a Firebase user into the local database and returns the current user profile.")
    @PostMapping("/sync")
    public ResponseEntity<Map<String, Object>> syncUser(
            @AuthenticationPrincipal FirebasePrincipal principal) {

        UserEntity user = userService.syncUser(principal);

        return ResponseEntity.ok(Map.of(
                "id",          user.getId(),
                "firebaseUid", user.getFirebaseUid(),
                "email",       user.getEmail() != null ? user.getEmail() : "",
                "displayName", user.getDisplayName() != null ? user.getDisplayName() : "",
                "photoUrl",    user.getPhotoUrl() != null ? user.getPhotoUrl() : ""
        ));
    }
}
