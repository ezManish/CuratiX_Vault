package com.curatix.vault.controller;

import com.curatix.vault.entity.BoardFileEntity;
import com.curatix.vault.security.FirebasePrincipal;
import com.curatix.vault.service.BoardFileService;
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
@RequestMapping("/api/boards/{boardId}/files")
@RequiredArgsConstructor
public class BoardFileController {

    private final BoardFileService boardFileService;

    @GetMapping
    public ResponseEntity<List<BoardFileEntity>> getFiles(
            @AuthenticationPrincipal FirebasePrincipal principal,
            @PathVariable Long boardId) {
        return ResponseEntity.ok(boardFileService.getFiles(principal.getUid(), boardId));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BoardFileEntity> uploadFile(
            @AuthenticationPrincipal FirebasePrincipal principal,
            @PathVariable Long boardId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "label", required = false) String label,
            @RequestParam(value = "fileType", defaultValue = "OTHER") String fileType)
            throws IOException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(boardFileService.uploadFile(principal.getUid(), boardId, file, label, fileType));
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<Map<String, String>> deleteFile(
            @AuthenticationPrincipal FirebasePrincipal principal,
            @PathVariable Long boardId,
            @PathVariable Long fileId) throws IOException {
        boardFileService.deleteFile(principal.getUid(), boardId, fileId);
        return ResponseEntity.ok(Map.of("message", "File deleted"));
    }
}
