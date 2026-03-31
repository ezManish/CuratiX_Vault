package com.curatix.vault.controller;

import com.curatix.vault.entity.BoardInvitationEntity;
import com.curatix.vault.security.FirebasePrincipal;
import com.curatix.vault.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/invitations")
@RequiredArgsConstructor
public class InvitationController {

    private final BoardService boardService;

    @GetMapping("/my")
    public ResponseEntity<List<BoardInvitationEntity>> getMyInvitations(@AuthenticationPrincipal FirebasePrincipal principal) {
        return ResponseEntity.ok(boardService.getMyInvitations(principal.getUid()));
    }

    @PostMapping("/{id}/respond")
    public ResponseEntity<Void> respondToInvitation(
            @AuthenticationPrincipal FirebasePrincipal principal,
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> body) {
        boolean accept = body.getOrDefault("accept", false);
        boardService.respondToInvitation(principal.getUid(), id, accept);
        return ResponseEntity.ok().build();
    }
}
