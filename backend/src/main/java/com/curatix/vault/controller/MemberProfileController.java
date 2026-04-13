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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/boards/{boardId}/profiles")
@RequiredArgsConstructor
@Tag(name = "Member Profiles", description = "Endpoints for managing board-specific member profiles (bios, skills, social links)")
public class MemberProfileController {

    private final MemberProfileService memberProfileService;

    @Operation(summary = "List Board Profiles", description = "Retrieves all member profiles associated with a specific board.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved profile list")
    @ApiResponse(responseCode = "403", description = "Forbidden - Not a member of the board")
    @GetMapping
    public ResponseEntity<List<MemberProfileEntity>> getProfiles(
            @AuthenticationPrincipal FirebasePrincipal principal,
            @PathVariable Long boardId) {
        return ResponseEntity.ok(memberProfileService.getProfiles(principal.getUid(), boardId));
    }

    @Operation(summary = "Add/Update Own Profile", description = "Manually adds or synchronizes a profile for the user on a specific board.")
    @ApiResponse(responseCode = "201", description = "Profile created successfully")
    @ApiResponse(responseCode = "400", description = "Bad Request - Validation failed")
    @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    @PostMapping
    public ResponseEntity<MemberProfileEntity> addProfile(
            @AuthenticationPrincipal FirebasePrincipal principal,
            @PathVariable Long boardId,
            @Valid @RequestBody MemberProfileRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(memberProfileService.addProfile(principal.getUid(), boardId, req));
    }

    @Operation(summary = "Edit Profile Info", description = "Updates fields like bio, skills, and social links for a specific board profile.")
    @ApiResponse(responseCode = "200", description = "Profile updated successfully")
    @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    @ApiResponse(responseCode = "404", description = "Profile not found")
    @PutMapping("/{profileId}")
    public ResponseEntity<MemberProfileEntity> updateProfile(
            @AuthenticationPrincipal FirebasePrincipal principal,
            @PathVariable Long boardId,
            @PathVariable Long profileId,
            @Valid @RequestBody MemberProfileRequest req) {
        return ResponseEntity.ok(
                memberProfileService.updateProfile(principal.getUid(), boardId, profileId, req));
    }

    @Operation(summary = "Delete Profile", description = "Deletes a specific member profile. Usually used when removing a member from a board.")
    @ApiResponse(responseCode = "200", description = "Profile deleted successfully")
    @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    @ApiResponse(responseCode = "404", description = "Profile not found")
    @DeleteMapping("/{profileId}")
    public ResponseEntity<Map<String, String>> deleteProfile(
            @AuthenticationPrincipal FirebasePrincipal principal,
            @PathVariable Long boardId,
            @PathVariable Long profileId) {
        memberProfileService.deleteProfile(principal.getUid(), boardId, profileId);
        return ResponseEntity.ok(Map.of("message", "Profile deleted"));
    }

    @Operation(summary = "Upload Profile Photo", description = "Uploads a board-specific profile photo to Cloudinary.")
    @ApiResponse(responseCode = "200", description = "Photo uploaded successfully")
    @ApiResponse(responseCode = "400", description = "Invalid image file")
    @ApiResponse(responseCode = "403", description = "Insufficient permissions")
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
