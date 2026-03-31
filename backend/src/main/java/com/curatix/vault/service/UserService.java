package com.curatix.vault.service;

import com.curatix.vault.entity.UserEntity;
import com.curatix.vault.repository.UserRepository;
import com.curatix.vault.security.FirebasePrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final org.springframework.context.ApplicationContext context;

    private MemberProfileService getMemberProfileService() {
        return context.getBean(MemberProfileService.class);
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    /**
     * Upserts a Firebase user into the MySQL users table.
     * Called on every login via /api/auth/sync.
     */
    @Transactional
    public UserEntity syncUser(FirebasePrincipal principal) {
        return userRepository.findByFirebaseUid(principal.getUid())
                .map(existing -> {
                    // Update mutable fields in case they changed in Firebase
                    existing.setDisplayName(principal.getDisplayName());
                    existing.setPhotoUrl(principal.getPhotoUrl());
                    return userRepository.save(existing);
                })
                .orElseGet(() -> userRepository.save(
                        UserEntity.builder()
                                .firebaseUid(principal.getUid())
                                .email(principal.getEmail())
                                .displayName(principal.getDisplayName())
                                .photoUrl(principal.getPhotoUrl())
                                .build()
                ));
    }

    public UserEntity getByFirebaseUid(String uid) {
        return userRepository.findByFirebaseUid(uid)
                .orElseThrow(() -> new com.curatix.vault.exception.ResourceNotFoundException(
                        "User not found. Please call /api/auth/sync first."));
    }

    @Transactional
    public UserEntity updateProfile(String uid, com.curatix.vault.dto.UserUpdateRequest req) {
        UserEntity user = getByFirebaseUid(uid);

        if (req.getDisplayName() != null) user.setDisplayName(req.getDisplayName());
        if (req.getAdmissionNo() != null) user.setAdmissionNo(req.getAdmissionNo());
        if (req.getEnrollmentNo() != null) user.setEnrollmentNo(req.getEnrollmentNo());
        if (req.getPhone() != null) user.setPhone(req.getPhone());
        if (req.getGithubUrl() != null) user.setGithubUrl(req.getGithubUrl());
        if (req.getLinkedinUrl() != null) user.setLinkedinUrl(req.getLinkedinUrl());
        if (req.getBio() != null) user.setBio(req.getBio());

        UserEntity saved = userRepository.save(user);
        getMemberProfileService().updateAllProfilesForUser(saved);
        return saved;
    }
}
