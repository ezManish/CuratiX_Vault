package com.curatix.vault.dto;

import lombok.Data;

@Data
public class UserUpdateRequest {
    private String displayName;
    private String admissionNo;
    private String enrollmentNo;
    private String phone;
    private String githubUrl;
    private String linkedinUrl;
    private String bio;
}
