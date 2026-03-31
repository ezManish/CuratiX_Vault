package com.curatix.vault.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "boards")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BoardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "cover_color", length = 7)
    private String coverColor;

    @Column(name = "cover_emoji", length = 10)
    private String coverEmoji;

    @Column(length = 100)
    private String platform;

    @Column(name = "event_date")
    private LocalDate eventDate;

    @Column(length = 255)
    private String venue;

    @Column(length = 255)
    private String theme;

    @Column(name = "team_name", length = 255)
    private String teamName;

    @Column(name = "problem_statement", columnDefinition = "TEXT")
    private String problemStatement;

    @Column(name = "project_idea", columnDefinition = "TEXT")
    private String projectIdea;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Result result = Result.PARTICIPATED;

    @Column(length = 255)
    private String prize;

    @Column(name = "submission_url", length = 500)
    private String submissionUrl;

    @Column(name = "repo_url", length = 500)
    private String repoUrl;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private UserEntity owner;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private boolean deleted = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum Result {
        PARTICIPATED, SHORTLISTED, TOP_N, WINNER
    }
}
