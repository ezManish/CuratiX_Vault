package com.curatix.vault.dto;

public class UserResponse {
    private Long id;
    private String firebaseUid;
    private String email;
    private String displayName;
    private String photoUrl;
    private String admissionNo;
    private String enrollmentNo;
    private String phone;
    private String githubUrl;
    private String linkedinUrl;
    private String bio;

    public UserResponse() {}

    public static UserResponseBuilder builder() {
        return new UserResponseBuilder();
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

    public static class UserResponseBuilder {
        private UserResponse instance = new UserResponse();
        public UserResponseBuilder id(Long id) { instance.setId(id); return this; }
        public UserResponseBuilder firebaseUid(String firebaseUid) { instance.setFirebaseUid(firebaseUid); return this; }
        public UserResponseBuilder email(String email) { instance.setEmail(email); return this; }
        public UserResponseBuilder displayName(String displayName) { instance.setDisplayName(displayName); return this; }
        public UserResponseBuilder photoUrl(String photoUrl) { instance.setPhotoUrl(photoUrl); return this; }
        public UserResponseBuilder admissionNo(String admissionNo) { instance.setAdmissionNo(admissionNo); return this; }
        public UserResponseBuilder enrollmentNo(String enrollmentNo) { instance.setEnrollmentNo(enrollmentNo); return this; }
        public UserResponseBuilder phone(String phone) { instance.setPhone(phone); return this; }
        public UserResponseBuilder githubUrl(String githubUrl) { instance.setGithubUrl(githubUrl); return this; }
        public UserResponseBuilder linkedinUrl(String linkedinUrl) { instance.setLinkedinUrl(linkedinUrl); return this; }
        public UserResponseBuilder bio(String bio) { instance.setBio(bio); return this; }
        public UserResponse build() { return instance; }
    }
}
