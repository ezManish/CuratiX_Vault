package com.curatix.vault.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "invite_links")
@EntityListeners(AuditingEntityListener.class)
public class InviteLinkEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private BoardEntity board;

    @Column(nullable = false, unique = true, length = 64)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BoardMemberEntity.Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private UserEntity createdBy; // The user who generated the link

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "max_uses")
    private Integer maxUses;

    @Column(name = "use_count", nullable = false)
    private int useCount = 0;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @LastModifiedBy
    @Column(name = "last_modified_by")
    private String lastModifiedBy; // Automated audit field

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public InviteLinkEntity() {}

    public static InviteLinkEntityBuilder builder() {
        return new InviteLinkEntityBuilder();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public BoardEntity getBoard() { return board; }
    public void setBoard(BoardEntity board) { this.board = board; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public BoardMemberEntity.Role getRole() { return role; }
    public void setRole(BoardMemberEntity.Role role) { this.role = role; }
    public UserEntity getCreatedBy() { return createdBy; }
    public void setCreatedBy(UserEntity createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    public Integer getMaxUses() { return maxUses; }
    public void setMaxUses(Integer maxUses) { this.maxUses = maxUses; }
    public int getUseCount() { return useCount; }
    public void setUseCount(int useCount) { this.useCount = useCount; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public String getLastModifiedBy() { return lastModifiedBy; }
    public void setLastModifiedBy(String lastModifiedBy) { this.lastModifiedBy = lastModifiedBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static class InviteLinkEntityBuilder {
        private InviteLinkEntity instance = new InviteLinkEntity();
        public InviteLinkEntityBuilder id(Long id) { instance.setId(id); return this; }
        public InviteLinkEntityBuilder board(BoardEntity board) { instance.setBoard(board); return this; }
        public InviteLinkEntityBuilder token(String token) { instance.setToken(token); return this; }
        public InviteLinkEntityBuilder role(BoardMemberEntity.Role role) { instance.setRole(role); return this; }
        public InviteLinkEntityBuilder createdBy(UserEntity createdBy) { instance.setCreatedBy(createdBy); return this; }
        public InviteLinkEntityBuilder expiresAt(LocalDateTime expiresAt) { instance.setExpiresAt(expiresAt); return this; }
        public InviteLinkEntityBuilder maxUses(Integer maxUses) { instance.setMaxUses(maxUses); return this; }
        public InviteLinkEntityBuilder useCount(int useCount) { instance.setUseCount(useCount); return this; }
        public InviteLinkEntityBuilder active(boolean active) { instance.setActive(active); return this; }
        public InviteLinkEntity build() { return instance; }
    }
}
