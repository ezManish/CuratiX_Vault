package com.curatix.vault.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "board_files")
@EntityListeners(AuditingEntityListener.class)
public class BoardFileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private BoardEntity board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by", nullable = false)
    private UserEntity uploadedBy;

    @Column(length = 255)
    private String label;

    @Enumerated(EnumType.STRING)
    @Column(name = "file_type")
    private FileType fileType;

    @Column(name = "cloudinary_url", nullable = false, length = 1000)
    private String cloudinaryUrl;

    @Column(name = "cloudinary_public_id", nullable = false, length = 500)
    private String cloudinaryPublicId;

    @Column(name = "original_filename", length = 255)
    private String originalFilename;

    @Column(name = "file_size_kb")
    private Integer fileSizeKb;

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "last_modified_by")
    private String lastModifiedBy;

    @CreationTimestamp
    @Column(name = "uploaded_at", updatable = false)
    private LocalDateTime uploadedAt;

    public BoardFileEntity() {}

    public static BoardFileEntityBuilder builder() {
        return new BoardFileEntityBuilder();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public BoardEntity getBoard() { return board; }
    public void setBoard(BoardEntity board) { this.board = board; }
    public UserEntity getUploadedBy() { return uploadedBy; }
    public void setUploadedBy(UserEntity uploadedBy) { this.uploadedBy = uploadedBy; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public FileType getFileType() { return fileType; }
    public void setFileType(FileType fileType) { this.fileType = fileType; }
    public String getCloudinaryUrl() { return cloudinaryUrl; }
    public void setCloudinaryUrl(String cloudinaryUrl) { this.cloudinaryUrl = cloudinaryUrl; }
    public String getCloudinaryPublicId() { return cloudinaryPublicId; }
    public void setCloudinaryPublicId(String cloudinaryPublicId) { this.cloudinaryPublicId = cloudinaryPublicId; }
    public String getOriginalFilename() { return originalFilename; }
    public void setOriginalFilename(String originalFilename) { this.originalFilename = originalFilename; }
    public Integer getFileSizeKb() { return fileSizeKb; }
    public void setFileSizeKb(Integer fileSizeKb) { this.fileSizeKb = fileSizeKb; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public String getLastModifiedBy() { return lastModifiedBy; }
    public void setLastModifiedBy(String lastModifiedBy) { this.lastModifiedBy = lastModifiedBy; }
    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }

    public static class BoardFileEntityBuilder {
        private BoardFileEntity instance = new BoardFileEntity();
        public BoardFileEntityBuilder board(BoardEntity board) { instance.setBoard(board); return this; }
        public BoardFileEntityBuilder uploadedBy(UserEntity uploadedBy) { instance.setUploadedBy(uploadedBy); return this; }
        public BoardFileEntityBuilder label(String label) { instance.setLabel(label); return this; }
        public BoardFileEntityBuilder fileType(FileType fileType) { instance.setFileType(fileType); return this; }
        public BoardFileEntityBuilder cloudinaryUrl(String cloudinaryUrl) { instance.setCloudinaryUrl(cloudinaryUrl); return this; }
        public BoardFileEntityBuilder cloudinaryPublicId(String cloudinaryPublicId) { instance.setCloudinaryPublicId(cloudinaryPublicId); return this; }
        public BoardFileEntityBuilder originalFilename(String originalFilename) { instance.setOriginalFilename(originalFilename); return this; }
        public BoardFileEntityBuilder fileSizeKb(Integer fileSizeKb) { instance.setFileSizeKb(fileSizeKb); return this; }
        public BoardFileEntity build() { return instance; }
    }

    public enum FileType {
        PRESENTATION, DOCUMENTATION, DESIGN, CODE, OTHER
    }
}
