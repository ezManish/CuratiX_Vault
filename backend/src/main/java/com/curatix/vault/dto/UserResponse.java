package com.curatix.vault.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
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
}
