package com.curatix.vault.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.curatix.vault.entity.BoardEntity;
import com.curatix.vault.entity.BoardFileEntity;
import com.curatix.vault.entity.UserEntity;
import com.curatix.vault.exception.ResourceNotFoundException;
import com.curatix.vault.repository.BoardFileRepository;
import com.curatix.vault.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Service class for managing file uploads and storage.
 * Interfaces with Cloudinary to store project assets like PRDs, pitch decks, and media.
 */
@Service
@RequiredArgsConstructor
public class BoardFileService {

    private final BoardFileRepository boardFileRepository;
    private final BoardRepository boardRepository;
    private final PermissionService permissionService;
    private final UserService userService;
    private final Cloudinary cloudinary;

    /**
     * Retrieves all files uploaded to a specific board.
     * 
     * @param firebaseUid The unique identifier of the requesting user.
     * @param boardId The ID of the board.
     * @return A list of BoardFileEntity objects.
     */
    public List<BoardFileEntity> getFiles(String firebaseUid, Long boardId) {
        var user = userService.getByFirebaseUid(firebaseUid);
        permissionService.getRole(boardId, user.getId());
        return boardFileRepository.findAllByBoardIdOrderByUploadedAtDesc(boardId);
    }

    /**
     * Uploads a file to Cloudinary and registers it in the board's vault.
     * Automatically determines the resource type (image, video, raw) for Cloudinary.
     * Requires EDITOR role or above.
     * 
     * @param firebaseUid The unique identifier of the uploader.
     * @param boardId The ID of the board.
     * @param file The file to upload.
     * @param label A human-readable label for the file.
     * @param fileType The category of the file (e.g. PRD, DESIGN).
     * @return The newly created and persisted BoardFileEntity.
     * @throws IOException if the file upload fails.
     */
    @Transactional
    public BoardFileEntity uploadFile(String firebaseUid, Long boardId,
                                      MultipartFile file, String label,
                                      String fileType) throws IOException {
        UserEntity user = userService.getByFirebaseUid(firebaseUid);
        permissionService.requireEditorOrAbove(boardId, user.getId());

        BoardEntity board = boardRepository.findByIdAndDeletedFalse(boardId)
                .orElseThrow(() -> new ResourceNotFoundException("Board not found"));

        // Upload to Cloudinary - use raw resource type for non-image files
        String resourceType = determineResourceType(file.getContentType());
        Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                "folder",         "curatix/boards/" + boardId,
                "resource_type",  resourceType,
                "use_filename",   true,
                "unique_filename", true
        ));

        BoardFileEntity.FileType type = BoardFileEntity.FileType.OTHER;
        try {
            if (fileType != null) type = BoardFileEntity.FileType.valueOf(fileType.toUpperCase());
        } catch (IllegalArgumentException ignored) {}

        BoardFileEntity boardFile = BoardFileEntity.builder()
                .board(board)
                .uploadedBy(user)
                .label(label)
                .fileType(type)
                .cloudinaryUrl((String) result.get("secure_url"))
                .cloudinaryPublicId((String) result.get("public_id"))
                .originalFilename(file.getOriginalFilename())
                .fileSizeKb((int) (file.getSize() / 1024))
                .build();

        return boardFileRepository.save(boardFile);
    }

    /**
     * Deletes a file from the vault and removes the asset from Cloudinary.
     * Requires EDITOR role or above.
     * 
     * @param firebaseUid The unique identifier of the user performing the deletion.
     * @param boardId The ID of the board.
     * @param fileId The ID of the file to delete.
     * @throws IOException if Cloudinary asset destruction fails.
     */
    @Transactional
    public void deleteFile(String firebaseUid, Long boardId, Long fileId) throws IOException {
        var user = userService.getByFirebaseUid(firebaseUid);
        permissionService.requireEditorOrAbove(boardId, user.getId());

        BoardFileEntity boardFile = boardFileRepository.findById(fileId)
                .filter(f -> f.getBoard().getId().equals(boardId))
                .orElseThrow(() -> new ResourceNotFoundException("File not found"));

        // Delete from Cloudinary
        String resourceType = boardFile.getCloudinaryUrl().contains("/video/") ? "video" : "raw";
        cloudinary.uploader().destroy(boardFile.getCloudinaryPublicId(),
                ObjectUtils.asMap("resource_type", resourceType));

        boardFileRepository.delete(boardFile);
    }

    private String determineResourceType(String contentType) {
        if (contentType == null) return "raw";
        if (contentType.startsWith("image/")) return "image";
        if (contentType.startsWith("video/")) return "video";
        return "raw";
    }
}
