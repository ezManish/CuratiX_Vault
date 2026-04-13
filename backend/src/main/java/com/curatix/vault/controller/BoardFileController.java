package com.curatix.vault.controller;

import com.curatix.vault.entity.BoardFileEntity;
import com.curatix.vault.security.FirebasePrincipal;
import com.curatix.vault.service.BoardFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/boards/{boardId}/files")
@RequiredArgsConstructor
@Tag(name = "Board Files", description = "Endpoints for managing project assets and Cloudinary file uploads")
public class BoardFileController {

    private final BoardFileService boardFileService;

    @Operation(summary = "List Board Files", description = "Retrieves a list of all assets uploaded to a specific board.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved file list")
    @ApiResponse(responseCode = "403", description = "Forbidden - Not a member of the board")
    @GetMapping
    public ResponseEntity<List<BoardFileEntity>> getFiles(
            @AuthenticationPrincipal FirebasePrincipal principal,
            @PathVariable Long boardId) {
        return ResponseEntity.ok(boardFileService.getFiles(principal.getUid(), boardId));
    }

    @Operation(summary = "Upload File", description = "Uploads a file to Cloudinary and registers it in the vault. Supports Multipart Form Data. Requires EDITOR role or above.")
    @ApiResponse(responseCode = "201", description = "File uploaded successfully")
    @ApiResponse(responseCode = "400", description = "Bad Request - Invalid file or data")
    @ApiResponse(responseCode = "403", description = "Insufficient permissions to upload")
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

    @Operation(summary = "Delete File", description = "Removes a file from the vault and deletes it from Cloudinary. Requires EDITOR role or above.")
    @ApiResponse(responseCode = "200", description = "File deleted successfully")
    @ApiResponse(responseCode = "403", description = "Insufficient permissions to delete")
    @ApiResponse(responseCode = "404", description = "File not found")
    @DeleteMapping("/{fileId}")
    public ResponseEntity<Map<String, String>> deleteFile(
            @AuthenticationPrincipal FirebasePrincipal principal,
            @PathVariable Long boardId,
            @PathVariable Long fileId) throws IOException {
        boardFileService.deleteFile(principal.getUid(), boardId, fileId);
        return ResponseEntity.ok(Map.of("message", "File deleted"));
    }
}
