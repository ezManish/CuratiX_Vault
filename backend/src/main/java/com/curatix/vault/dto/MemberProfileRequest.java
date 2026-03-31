package com.curatix.vault.dto;

import lombok.Data;

@Data
public class MemberProfileRequest {
    private String fullName;
    private String admissionNo;
    private String enrollmentNo;
    private String email;
    private String phone;
    private String githubUrl;
    private String linkedinUrl;
    private String repoUrl;
    private String roleInTeam;
    private String yearBranch;
    private String skills;
    private String bio;
}
