package com.curatix.vault.controller;

import com.curatix.vault.dto.MemberProfileRequest;
import com.curatix.vault.entity.MemberProfileEntity;
import com.curatix.vault.security.FirebasePrincipal;
import com.curatix.vault.service.MemberProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/boards/{boardId}/profiles")
@RequiredArgsConstructor
public class MemberProfileController {

    private final MemberProfileService memberProfileService;

    @GetMapping
    public ResponseEntity<List<MemberProfileEntity>> getProfiles(
            @AuthenticationPrincipal FirebasePrincipal principal,
            @PathVariable Long boardId) {
        return ResponseEntity.ok(memberProfileService.getProfiles(principal.getUid(), boardId));
    }

    @PostMapping
    public ResponseEntity<MemberProfileEntity> addProfile(
            @AuthenticationPrincipal FirebasePrincipal principal,
            @PathVariable Long boardId,
            @RequestBody MemberProfileRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(memberProfileService.addProfile(principal.getUid(), boardId, req));
    }

    @PutMapping("/{profileId}")
    public ResponseEntity<MemberProfileEntity> updateProfile(
            @AuthenticationPrincipal FirebasePrincipal principal,
            @PathVariable Long boardId,
            @PathVariable Long profileId,
            @RequestBody MemberProfileRequest req) {
        return ResponseEntity.ok(
                memberProfileService.updateProfile(principal.getUid(), boardId, profileId, req));
    }

    @DeleteMapping("/{profileId}")
    public ResponseEntity<Map<String, String>> deleteProfile(
            @AuthenticationPrincipal FirebasePrincipal principal,
            @PathVariable Long boardId,
            @PathVariable Long profileId) {
        memberProfileService.deleteProfile(principal.getUid(), boardId, profileId);
        return ResponseEntity.ok(Map.of("message", "Profile deleted"));
    }

    @PostMapping(value = "/{profileId}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MemberProfileEntity> uploadPhoto(
            @AuthenticationPrincipal FirebasePrincipal principal,
            @PathVariable Long boardId,
            @PathVariable Long profileId,
            @RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(
                memberProfileService.uploadProfilePhoto(principal.getUid(), boardId, profileId, file));
    }
}
