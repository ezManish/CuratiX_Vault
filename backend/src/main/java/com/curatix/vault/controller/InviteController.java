package com.curatix.vault.controller;

import com.curatix.vault.entity.InviteLinkEntity;
import com.curatix.vault.security.FirebasePrincipal;
import com.curatix.vault.service.InviteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "Invite Links", description = "Endpoints for generating and joining boards via shareable invite tokens")
public class InviteController {

    private final InviteService inviteService;

    /** Generate a new invite link for a board */
    @Operation(summary = "Generate Invite Link", description = "Creates a shareable invite token with a specific role, expiry, and usage limit. Requires OWNER role.")
    @PostMapping("/api/boards/{boardId}/invite/link")
    public ResponseEntity<InviteLinkEntity> generateLink(
            @AuthenticationPrincipal FirebasePrincipal principal,
            @PathVariable Long boardId,
            @RequestBody Map<String, Object> body) {

        String role = (String) body.getOrDefault("role", "VIEWER");
        Integer maxUses = body.get("maxUses") != null
                ? Integer.parseInt(body.get("maxUses").toString()) : null;

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inviteService.generateInviteLink(principal.getUid(), boardId, role, maxUses));
    }

    /** List all active invite links for a board */
    @Operation(summary = "Get Active Invite Links", description = "Retrieves all currently active and unexpired invite links for a board. Requires OWNER role.")
    @GetMapping("/api/boards/{boardId}/invite/links")
    public ResponseEntity<List<InviteLinkEntity>> getActiveLinks(
            @AuthenticationPrincipal FirebasePrincipal principal,
            @PathVariable Long boardId) {
        return ResponseEntity.ok(inviteService.getActiveLinks(principal.getUid(), boardId));
    }

    /** Revoke an invite link */
    @Operation(summary = "Revoke Invite Link", description = "Deactivates an existing invite link. Requires OWNER role.")
    @DeleteMapping("/api/boards/{boardId}/invite/links/{linkId}")
    public ResponseEntity<Map<String, String>> revokeLink(
            @AuthenticationPrincipal FirebasePrincipal principal,
            @PathVariable Long boardId,
            @PathVariable Long linkId) {
        inviteService.revokeLink(principal.getUid(), boardId, linkId);
        return ResponseEntity.ok(Map.of("message", "Invite link revoked"));
    }

    /** Public: preview invite link info (board name, role) before joining */
    @Operation(summary = "Preview Invite Link", description = "Public endpoint to check board name and assigned role for a token before joining.")
    @GetMapping("/api/invite/preview/{token}")
    public ResponseEntity<Map<String, Object>> previewLink(@PathVariable String token) {
        return ResponseEntity.ok(inviteService.previewLink(token));
    }

    /** Authenticated: join a board via invite token */
    @Operation(summary = "Join Board via Token", description = "Registers the authenticated user to a board using a valid invite token.")
    @PostMapping("/api/invite/join/{token}")
    public ResponseEntity<Map<String, Object>> joinViaLink(
            @AuthenticationPrincipal FirebasePrincipal principal,
            @PathVariable String token) {
        return ResponseEntity.ok(inviteService.joinViaLink(principal.getUid(), token));
    }
}
