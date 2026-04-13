package com.curatix.vault.service;

import com.curatix.vault.entity.UserEntity;
import com.curatix.vault.repository.UserRepository;
import com.curatix.vault.security.FirebasePrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing global User identity.
 * Handles synchronization between Firebase Authentication and the local database,
 * as well as global profile updates that propagate to board-specific member cards.
 */
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
     * Synchronizes a user from Firebase Auth into the local database.
     * Upserts a UserEntity based on the Firebase uid.
     * 
     * @param principal The authenticated principal containing Firebase user data.
     * @return The synchronized UserEntity.
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

    /**
     * Fetches a UserEntity by their unique Firebase identifier.
     * 
     * @param uid The Firebase unique ID string.
     * @return The UserEntity if found.
     * @throws ResourceNotFoundException if no user is synchronized yet.
     */
    public UserEntity getByFirebaseUid(String uid) {
        return userRepository.findByFirebaseUid(uid)
                .orElseThrow(() -> new com.curatix.vault.exception.ResourceNotFoundException(
                        "User not found. Please call /api/auth/sync first."));
    }

    /**
     * Updates the user's global professional identity.
     * This update automatically propagates changes to all board-specific member profiles
     * to ensure data consistency across the platform.
     * 
     * @param uid The Firebase unique ID of the user.
     * @param req The update request DTO.
     * @return The updated and persisted UserEntity.
     */
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
