package com.curatix.vault.controller;

import com.curatix.vault.dto.UserResponse;
import com.curatix.vault.dto.UserUpdateRequest;
import com.curatix.vault.entity.UserEntity;
import com.curatix.vault.security.FirebasePrincipal;
import com.curatix.vault.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyProfile(@AuthenticationPrincipal FirebasePrincipal principal) {
        UserEntity user = userService.getByFirebaseUid(principal.getUid());
        return ResponseEntity.ok(toResponse(user));
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateMyProfile(
            @AuthenticationPrincipal FirebasePrincipal principal,
            @RequestBody UserUpdateRequest req) {
        UserEntity user = userService.updateProfile(principal.getUid(), req);
        return ResponseEntity.ok(toResponse(user));
    }

    private UserResponse toResponse(UserEntity user) {
        return UserResponse.builder()
                .id(user.getId())
                .firebaseUid(user.getFirebaseUid())
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .photoUrl(user.getPhotoUrl())
                .admissionNo(user.getAdmissionNo())
                .enrollmentNo(user.getEnrollmentNo())
                .phone(user.getPhone())
                .githubUrl(user.getGithubUrl())
                .linkedinUrl(user.getLinkedinUrl())
                .bio(user.getBio())
                .build();
    }
}
