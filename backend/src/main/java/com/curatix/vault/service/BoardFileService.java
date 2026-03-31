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

@Service
@RequiredArgsConstructor
public class BoardFileService {

    private final BoardFileRepository boardFileRepository;
    private final BoardRepository boardRepository;
    private final PermissionService permissionService;
    private final UserService userService;
    private final Cloudinary cloudinary;

    public List<BoardFileEntity> getFiles(String firebaseUid, Long boardId) {
        var user = userService.getByFirebaseUid(firebaseUid);
        permissionService.getRole(boardId, user.getId());
        return boardFileRepository.findAllByBoardIdOrderByUploadedAtDesc(boardId);
    }

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
