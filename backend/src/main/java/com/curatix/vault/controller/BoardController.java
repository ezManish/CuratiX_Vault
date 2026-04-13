package com.curatix.vault.controller;

import com.curatix.vault.dto.BoardMemberResponse;
import com.curatix.vault.dto.CreateBoardRequest;
import com.curatix.vault.entity.BoardEntity;
import com.curatix.vault.entity.BoardMemberEntity;
import com.curatix.vault.security.FirebasePrincipal;
import com.curatix.vault.service.BoardService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
@Tag(name = "Boards", description = "Operations related to hackathon boards, membership, and permissions")
public class BoardController {

    private final BoardService boardService;

    @Operation(summary = "Get My Boards", description = "Retrieves a list of all boards the authenticated user is a member of.")
    @GetMapping
    public ResponseEntity<List<BoardEntity>> getMyBoards(
            @AuthenticationPrincipal FirebasePrincipal principal) {
        return ResponseEntity.ok(boardService.getBoardsForUser(principal.getUid()));
    }

    @Operation(summary = "Create Board", description = "Creates a new board and assigns the authenticated user as the OWNER.")
    @PostMapping
    public ResponseEntity<BoardEntity> createBoard(
            @AuthenticationPrincipal FirebasePrincipal principal,
            @Valid @RequestBody CreateBoardRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(boardService.createBoard(principal.getUid(), req));
    }

    @Operation(summary = "Get Board Details", description = "Retrieves full details of a specific board if the user is a member.")
    @ApiResponse(responseCode = "403", description = "User is not a member of this board")
    @ApiResponse(responseCode = "404", description = "Board not found")
    @GetMapping("/{boardId}")
    public ResponseEntity<BoardEntity> getBoard(
            @AuthenticationPrincipal FirebasePrincipal principal,
            @PathVariable Long boardId) {
        return ResponseEntity.ok(boardService.getBoard(principal.getUid(), boardId));
    }

    @Operation(summary = "Update Board Settings", description = "Updates metadata for a board. Requires EDITOR role or above.")
    @PutMapping("/{boardId}")
    public ResponseEntity<BoardEntity> updateBoard(
            @AuthenticationPrincipal FirebasePrincipal principal,
            @PathVariable Long boardId,
            @Valid @RequestBody CreateBoardRequest req) {
        return ResponseEntity.ok(boardService.updateBoard(principal.getUid(), boardId, req));
    }

    @Operation(summary = "Delete Board", description = "Soft-deletes a board. Requires OWNER role.")
    @DeleteMapping("/{boardId}")
    public ResponseEntity<Map<String, String>> deleteBoard(
            @AuthenticationPrincipal FirebasePrincipal principal,
            @PathVariable Long boardId) {
        boardService.deleteBoard(principal.getUid(), boardId);
        return ResponseEntity.ok(Map.of("message", "Board deleted successfully"));
    }

    @Operation(summary = "List Board Members", description = "Retrieves all members of a board, including their roles and profiles.")
    @GetMapping("/{boardId}/members")
    public ResponseEntity<List<BoardMemberResponse>> getBoardMembers(
            @AuthenticationPrincipal FirebasePrincipal principal,
            @PathVariable Long boardId) {
        return ResponseEntity.ok(boardService.getBoardMembers(principal.getUid(), boardId));
    }

    @Operation(summary = "Change Member Role", description = "Updates the role of a member (e.g. EDITOR to VIEWER). Requires OWNER role.")
    @PutMapping("/{boardId}/members/{userId}/role")
    public ResponseEntity<Map<String, String>> changeMemberRole(
            @AuthenticationPrincipal FirebasePrincipal principal,
            @PathVariable Long boardId,
            @PathVariable Long userId,
            @RequestBody Map<String, String> body) {
        BoardMemberEntity.Role newRole = BoardMemberEntity.Role.valueOf(body.get("role").toUpperCase());
        boardService.changeMemberRole(principal.getUid(), boardId, userId, newRole);
        return ResponseEntity.ok(Map.of("message", "Role updated"));
    }

    @Operation(summary = "Add Member by Email", description = "Invites a user to the board by email. If they have a vault account, they are added immediately.")
    @PostMapping("/{boardId}/members/email")
    public ResponseEntity<Map<String, String>> addMemberByEmail(
            @AuthenticationPrincipal FirebasePrincipal principal,
            @PathVariable Long boardId,
            @RequestBody Map<String, String> body) {
        String email = body.get("email");
        BoardMemberEntity.Role role = BoardMemberEntity.Role.valueOf(body.getOrDefault("role", "EDITOR").toUpperCase());
        boardService.addMemberByEmail(principal.getUid(), boardId, email, role);
        return ResponseEntity.ok(Map.of("message", "Member added and profile synced"));
    }

    @Operation(summary = "Remove Member", description = "Removes a user from the board. Requires OWNER role.")
    @DeleteMapping("/{boardId}/members/{userId}")
    public ResponseEntity<Map<String, String>> removeMember(
            @AuthenticationPrincipal FirebasePrincipal principal,
            @PathVariable Long boardId,
            @PathVariable Long userId) {
        boardService.removeMember(principal.getUid(), boardId, userId);
        return ResponseEntity.ok(Map.of("message", "Member removed"));
    }
}
