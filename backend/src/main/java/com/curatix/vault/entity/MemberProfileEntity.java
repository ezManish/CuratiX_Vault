package com.curatix.vault.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "member_profiles")
@EntityListeners(AuditingEntityListener.class)
public class MemberProfileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private BoardEntity board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

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
    private String skills;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(name = "photo_url", length = 500)
    private String photoUrl;

    @Column(name = "cloudinary_public_id", length = 500)
    private String cloudinaryPublicId;

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "last_modified_by")
    private String lastModifiedBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public MemberProfileEntity() {}

    public static MemberProfileEntityBuilder builder() {
        return new MemberProfileEntityBuilder();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public BoardEntity getBoard() { return board; }
    public void setBoard(BoardEntity board) { this.board = board; }
    public UserEntity getUser() { return user; }
    public void setUser(UserEntity user) { this.user = user; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getAdmissionNo() { return admissionNo; }
    public void setAdmissionNo(String admissionNo) { this.admissionNo = admissionNo; }
    public String getEnrollmentNo() { return enrollmentNo; }
    public void setEnrollmentNo(String enrollmentNo) { this.enrollmentNo = enrollmentNo; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getGithubUrl() { return githubUrl; }
    public void setGithubUrl(String githubUrl) { this.githubUrl = githubUrl; }
    public String getLinkedinUrl() { return linkedinUrl; }
    public void setLinkedinUrl(String linkedinUrl) { this.linkedinUrl = linkedinUrl; }
    public String getRepoUrl() { return repoUrl; }
    public void setRepoUrl(String repoUrl) { this.repoUrl = repoUrl; }
    public String getRoleInTeam() { return roleInTeam; }
    public void setRoleInTeam(String roleInTeam) { this.roleInTeam = roleInTeam; }
    public String getYearBranch() { return yearBranch; }
    public void setYearBranch(String yearBranch) { this.yearBranch = yearBranch; }
    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    public String getCloudinaryPublicId() { return cloudinaryPublicId; }
    public void setCloudinaryPublicId(String cloudinaryPublicId) { this.cloudinaryPublicId = cloudinaryPublicId; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public String getLastModifiedBy() { return lastModifiedBy; }
    public void setLastModifiedBy(String lastModifiedBy) { this.lastModifiedBy = lastModifiedBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static class MemberProfileEntityBuilder {
        private MemberProfileEntity instance = new MemberProfileEntity();
        public MemberProfileEntityBuilder id(Long id) { instance.setId(id); return this; }
        public MemberProfileEntityBuilder board(BoardEntity board) { instance.setBoard(board); return this; }
        public MemberProfileEntityBuilder user(UserEntity user) { instance.setUser(user); return this; }
        public MemberProfileEntityBuilder fullName(String fullName) { instance.setFullName(fullName); return this; }
        public MemberProfileEntityBuilder admissionNo(String admissionNo) { instance.setAdmissionNo(admissionNo); return this; }
        public MemberProfileEntityBuilder enrollmentNo(String enrollmentNo) { instance.setEnrollmentNo(enrollmentNo); return this; }
        public MemberProfileEntityBuilder email(String email) { instance.setEmail(email); return this; }
        public MemberProfileEntityBuilder phone(String phone) { instance.setPhone(phone); return this; }
        public MemberProfileEntityBuilder githubUrl(String githubUrl) { instance.setGithubUrl(githubUrl); return this; }
        public MemberProfileEntityBuilder linkedinUrl(String linkedinUrl) { instance.setLinkedinUrl(linkedinUrl); return this; }
        public MemberProfileEntityBuilder repoUrl(String repoUrl) { instance.setRepoUrl(repoUrl); return this; }
        public MemberProfileEntityBuilder roleInTeam(String roleInTeam) { instance.setRoleInTeam(roleInTeam); return this; }
        public MemberProfileEntityBuilder yearBranch(String yearBranch) { instance.setYearBranch(yearBranch); return this; }
        public MemberProfileEntityBuilder skills(String skills) { instance.setSkills(skills); return this; }
        public MemberProfileEntityBuilder bio(String bio) { instance.setBio(bio); return this; }
        public MemberProfileEntity build() { return instance; }
    }
}
