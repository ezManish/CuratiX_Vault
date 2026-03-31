package com.curatix.vault.controller;

import com.curatix.vault.dto.BoardMemberResponse;
import com.curatix.vault.dto.CreateBoardRequest;
import com.curatix.vault.entity.BoardEntity;
import com.curatix.vault.entity.BoardMemberEntity;
import com.curatix.vault.security.FirebasePrincipal;
import com.curatix.vault.service.BoardService;
import jakarta.validation.Valid;
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
public class BoardController {

    private final BoardService boardService;

    @GetMapping
    public ResponseEntity<List<BoardEntity>> getMyBoards(
            @AuthenticationPrincipal FirebasePrincipal principal) {
        return ResponseEntity.ok(boardService.getBoardsForUser(principal.getUid()));
    }

    @PostMapping
    public ResponseEntity<BoardEntity> createBoard(
            @AuthenticationPrincipal FirebasePrincipal principal,
            @Valid @RequestBody CreateBoardRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(boardService.createBoard(principal.getUid(), req));
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<BoardEntity> getBoard(
            @AuthenticationPrincipal FirebasePrincipal principal,
            @PathVariable Long boardId) {
        return ResponseEntity.ok(boardService.getBoard(principal.getUid(), boardId));
    }

    @PutMapping("/{boardId}")
    public ResponseEntity<BoardEntity> updateBoard(
            @AuthenticationPrincipal FirebasePrincipal principal,
            @PathVariable Long boardId,
            @Valid @RequestBody CreateBoardRequest req) {
        return ResponseEntity.ok(boardService.updateBoard(principal.getUid(), boardId, req));
    }

    @DeleteMapping("/{boardId}")
    public ResponseEntity<Map<String, String>> deleteBoard(
            @AuthenticationPrincipal FirebasePrincipal principal,
            @PathVariable Long boardId) {
        boardService.deleteBoard(principal.getUid(), boardId);
        return ResponseEntity.ok(Map.of("message", "Board deleted successfully"));
    }

    @GetMapping("/{boardId}/members")
    public ResponseEntity<List<BoardMemberResponse>> getBoardMembers(
            @AuthenticationPrincipal FirebasePrincipal principal,
            @PathVariable Long boardId) {
        return ResponseEntity.ok(boardService.getBoardMembers(principal.getUid(), boardId));
    }

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

    @DeleteMapping("/{boardId}/members/{userId}")
    public ResponseEntity<Map<String, String>> removeMember(
            @AuthenticationPrincipal FirebasePrincipal principal,
            @PathVariable Long boardId,
            @PathVariable Long userId) {
        boardService.removeMember(principal.getUid(), boardId, userId);
        return ResponseEntity.ok(Map.of("message", "Member removed"));
    }
}
