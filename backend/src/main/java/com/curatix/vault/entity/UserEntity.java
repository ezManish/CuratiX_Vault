package com.curatix.vault.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "firebase_uid", nullable = false, unique = true, length = 128)
    private String firebaseUid;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "photo_url", length = 500)
    private String photoUrl;

    @Column(name = "admission_no", length = 50)
    private String admissionNo;

    @Column(name = "enrollment_no", length = 50)
    private String enrollmentNo;

    @Column(length = 20)
    private String phone;

    @Column(name = "github_url", length = 500)
    private String githubUrl;

    @Column(name = "linkedin_url", length = 500)
    private String linkedinUrl;

    @Column(columnDefinition = "TEXT")
    private String bio;

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

    public UserEntity() {}

    public static UserEntityBuilder builder() {
        return new UserEntityBuilder();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFirebaseUid() { return firebaseUid; }
    public void setFirebaseUid(String firebaseUid) { this.firebaseUid = firebaseUid; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    public String getAdmissionNo() { return admissionNo; }
    public void setAdmissionNo(String admissionNo) { this.admissionNo = admissionNo; }
    public String getEnrollmentNo() { return enrollmentNo; }
    public void setEnrollmentNo(String enrollmentNo) { this.enrollmentNo = enrollmentNo; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getGithubUrl() { return githubUrl; }
    public void setGithubUrl(String githubUrl) { this.githubUrl = githubUrl; }
    public String getLinkedinUrl() { return linkedinUrl; }
    public void setLinkedinUrl(String linkedinUrl) { this.linkedinUrl = linkedinUrl; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public String getLastModifiedBy() { return lastModifiedBy; }
    public void setLastModifiedBy(String lastModifiedBy) { this.lastModifiedBy = lastModifiedBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static class UserEntityBuilder {
        private UserEntity instance = new UserEntity();
        public UserEntityBuilder id(Long id) { instance.setId(id); return this; }
        public UserEntityBuilder firebaseUid(String firebaseUid) { instance.setFirebaseUid(firebaseUid); return this; }
        public UserEntityBuilder email(String email) { instance.setEmail(email); return this; }
        public UserEntityBuilder displayName(String displayName) { instance.setDisplayName(displayName); return this; }
        public UserEntityBuilder photoUrl(String photoUrl) { instance.setPhotoUrl(photoUrl); return this; }
        public UserEntityBuilder bio(String bio) { instance.setBio(bio); return this; }
        public UserEntityBuilder admissionNo(String admissionNo) { instance.setAdmissionNo(admissionNo); return this; }
        public UserEntityBuilder enrollmentNo(String enrollmentNo) { instance.setEnrollmentNo(enrollmentNo); return this; }
        public UserEntityBuilder phone(String phone) { instance.setPhone(phone); return this; }
        public UserEntityBuilder githubUrl(String githubUrl) { instance.setGithubUrl(githubUrl); return this; }
        public UserEntityBuilder linkedinUrl(String linkedinUrl) { instance.setLinkedinUrl(linkedinUrl); return this; }
        public UserEntity build() { return instance; }
    }
}
