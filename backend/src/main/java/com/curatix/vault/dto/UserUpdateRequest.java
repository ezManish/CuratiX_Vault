package com.curatix.vault.dto;

import jakarta.validation.constraints.Size;

public class UserUpdateRequest {
    @Size(max = 100)
    private String displayName;

    @Size(max = 500)
    private String photoUrl;

    @Size(max = 50)
    private String admissionNo;

    @Size(max = 50)
    private String enrollmentNo;

    @Size(max = 20)
    private String phone;

    @Size(max = 500)
    private String githubUrl;

    @Size(max = 500)
    private String linkedinUrl;

    private String bio;

    public UserUpdateRequest() {}

    // Getters and Setters
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
}
