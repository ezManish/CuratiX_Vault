package com.curatix.vault.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "board_invitations",
       uniqueConstraints = @UniqueConstraint(columnNames = {"board_id", "recipient_email", "status"}))
@EntityListeners(AuditingEntityListener.class)
public class BoardInvitationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private BoardEntity board;

    @Column(name = "recipient_email", nullable = false)
    private String recipientEmail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BoardMemberEntity.Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inviter_id", nullable = false)
    private UserEntity inviter;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PENDING;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    @LastModifiedBy
    @Column(name = "last_modified_by")
    private String lastModifiedBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public BoardInvitationEntity() {}

    public static BoardInvitationEntityBuilder builder() {
        return new BoardInvitationEntityBuilder();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public BoardEntity getBoard() { return board; }
    public void setBoard(BoardEntity board) { this.board = board; }
    public String getRecipientEmail() { return recipientEmail; }
    public void setRecipientEmail(String recipientEmail) { this.recipientEmail = recipientEmail; }
    public BoardMemberEntity.Role getRole() { return role; }
    public void setRole(BoardMemberEntity.Role role) { this.role = role; }
    public UserEntity getInviter() { return inviter; }
    public void setInviter(UserEntity inviter) { this.inviter = inviter; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public LocalDateTime getRespondedAt() { return respondedAt; }
    public void setRespondedAt(LocalDateTime respondedAt) { this.respondedAt = respondedAt; }
    public String getLastModifiedBy() { return lastModifiedBy; }
    public void setLastModifiedBy(String lastModifiedBy) { this.lastModifiedBy = lastModifiedBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static class BoardInvitationEntityBuilder {
        private BoardInvitationEntity instance = new BoardInvitationEntity();
        public BoardInvitationEntityBuilder id(Long id) { instance.setId(id); return this; }
        public BoardInvitationEntityBuilder board(BoardEntity board) { instance.setBoard(board); return this; }
        public BoardInvitationEntityBuilder recipientEmail(String recipientEmail) { instance.setRecipientEmail(recipientEmail); return this; }
        public BoardInvitationEntityBuilder role(BoardMemberEntity.Role role) { instance.setRole(role); return this; }
        public BoardInvitationEntityBuilder inviter(UserEntity inviter) { instance.setInviter(inviter); return this; }
        public BoardInvitationEntityBuilder status(Status status) { instance.setStatus(status); return this; }
        public BoardInvitationEntity build() { return instance; }
    }

    public enum Status {
        PENDING, ACCEPTED, DECLINED
    }
}
