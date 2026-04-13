package com.curatix.vault.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public class MemberProfileRequest {
    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100)
    private String fullName;

    @Size(max = 50)
    private String admissionNo;

    @Size(max = 50)
    private String enrollmentNo;

    @Email(message = "Invalid email format")
    private String email;

    @Size(max = 20)
    private String phone;

    @URL(message = "Invalid GitHub URL")
    private String githubUrl;

    @URL(message = "Invalid LinkedIn URL")
    private String linkedinUrl;

    @URL(message = "Invalid Repository URL")
    private String repoUrl;

    @Size(max = 100)
    private String roleInTeam;

    @Size(max = 100)
    private String yearBranch;

    @Size(max = 1000)
    private String skills;

    @Size(max = 2000)
    private String bio;

    public MemberProfileRequest() {}

    // Getters and Setters
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
}
