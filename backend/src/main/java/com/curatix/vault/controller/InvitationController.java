package com.curatix.vault.controller;

import com.curatix.vault.entity.BoardInvitationEntity;
import com.curatix.vault.security.FirebasePrincipal;
import com.curatix.vault.service.BoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/invitations")
@RequiredArgsConstructor
@Tag(name = "Invitations", description = "Endpoints for responding to board invitations")
public class InvitationController {

    private final BoardService boardService;

    @Operation(summary = "Get My Invitations", description = "Retrieves all pending board invitations for the authenticated user.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved invitations")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @GetMapping("/my")
    public ResponseEntity<List<BoardInvitationEntity>> getMyInvitations(@AuthenticationPrincipal FirebasePrincipal principal) {
        return ResponseEntity.ok(boardService.getMyInvitations(principal.getUid()));
    }

    @Operation(summary = "Respond to Invitation", description = "Accepts or declines a board invitation.")
    @ApiResponse(responseCode = "200", description = "Response processed successfully")
    @ApiResponse(responseCode = "404", description = "Invitation not found")
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
