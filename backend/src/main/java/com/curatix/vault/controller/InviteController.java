package com.curatix.vault.controller;

import com.curatix.vault.entity.InviteLinkEntity;
import com.curatix.vault.security.FirebasePrincipal;
import com.curatix.vault.service.InviteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class InviteController {

    private final InviteService inviteService;

    /** Generate a new invite link for a board */
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
    @GetMapping("/api/boards/{boardId}/invite/links")
    public ResponseEntity<List<InviteLinkEntity>> getActiveLinks(
            @AuthenticationPrincipal FirebasePrincipal principal,
            @PathVariable Long boardId) {
        return ResponseEntity.ok(inviteService.getActiveLinks(principal.getUid(), boardId));
    }

    /** Revoke an invite link */
    @DeleteMapping("/api/boards/{boardId}/invite/links/{linkId}")
    public ResponseEntity<Map<String, String>> revokeLink(
            @AuthenticationPrincipal FirebasePrincipal principal,
            @PathVariable Long boardId,
            @PathVariable Long linkId) {
        inviteService.revokeLink(principal.getUid(), boardId, linkId);
        return ResponseEntity.ok(Map.of("message", "Invite link revoked"));
    }

    /** Public: preview invite link info (board name, role) before joining */
    @GetMapping("/api/invite/preview/{token}")
    public ResponseEntity<Map<String, Object>> previewLink(@PathVariable String token) {
        return ResponseEntity.ok(inviteService.previewLink(token));
    }

    /** Authenticated: join a board via invite token */
    @PostMapping("/api/invite/join/{token}")
    public ResponseEntity<Map<String, Object>> joinViaLink(
            @AuthenticationPrincipal FirebasePrincipal principal,
            @PathVariable String token) {
        return ResponseEntity.ok(inviteService.joinViaLink(principal.getUid(), token));
    }
}
