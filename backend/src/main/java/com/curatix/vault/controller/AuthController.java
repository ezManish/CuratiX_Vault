package com.curatix.vault.controller;

import com.curatix.vault.entity.UserEntity;
import com.curatix.vault.security.FirebasePrincipal;
import com.curatix.vault.service.UserService;
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
public class AuthController {

    private final UserService userService;

    /**
     * POST /api/auth/sync
     * Called on every login. Upserts Firebase user into MySQL.
     * Returns the user record (id, email, displayName, photoUrl).
     */
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
