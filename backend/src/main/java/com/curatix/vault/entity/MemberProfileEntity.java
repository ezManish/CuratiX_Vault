package com.curatix.vault.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "member_profiles")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MemberProfileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private BoardEntity board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user; // nullable — can add non-platform members

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "admission_no", length = 50)
    private String admissionNo;

    @Column(name = "enrollment_no", length = 50)
    private String enrollmentNo;

    @Column(length = 255)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(name = "github_url", length = 500)
    private String githubUrl;

    @Column(name = "linkedin_url", length = 500)
    private String linkedinUrl;

    @Column(name = "repo_url", length = 500)
    private String repoUrl;

    @Column(name = "role_in_team", length = 100)
    private String roleInTeam;

    @Column(name = "year_branch", length = 100)
    private String yearBranch;

    @Column(columnDefinition = "TEXT")
    private String skills; // comma-separated

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(name = "photo_url", length = 500)
    private String photoUrl;

    @Column(name = "cloudinary_public_id", length = 500)
    private String cloudinaryPublicId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
