package com.curatix.vault.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.curatix.vault.dto.MemberProfileRequest;
import com.curatix.vault.entity.BoardEntity;
import com.curatix.vault.entity.MemberProfileEntity;
import com.curatix.vault.exception.BadRequestException;
import com.curatix.vault.exception.ResourceNotFoundException;
import com.curatix.vault.repository.BoardRepository;
import com.curatix.vault.repository.MemberProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MemberProfileService {

    private final MemberProfileRepository memberProfileRepository;
    private final BoardRepository boardRepository;
    private final PermissionService permissionService;
    private final UserService userService;
    private final Cloudinary cloudinary;

    @Transactional
    public MemberProfileEntity syncFromUser(BoardEntity board, com.curatix.vault.entity.UserEntity user) {
        // Find existing profile for this user on this board, or create new
        MemberProfileEntity profile = memberProfileRepository.findByBoardIdAndUserId(board.getId(), user.getId())
                .orElse(new MemberProfileEntity());

        profile.setBoard(board);
        profile.setUser(user);

        // Sync fields from Global User Profile
        // Use "N/A" if field is empty/null, except for Full Name (DisplayName)
        profile.setFullName(user.getDisplayName() != null ? user.getDisplayName() : "Anonymous User");
        profile.setAdmissionNo(user.getAdmissionNo() != null ? user.getAdmissionNo() : "N/A");
        profile.setEnrollmentNo(user.getEnrollmentNo() != null ? user.getEnrollmentNo() : "N/A");
        profile.setEmail(user.getEmail());
        profile.setPhone(user.getPhone() != null ? user.getPhone() : "N/A");
        profile.setGithubUrl(user.getGithubUrl() != null ? user.getGithubUrl() : "N/A");
        profile.setLinkedinUrl(user.getLinkedinUrl() != null ? user.getLinkedinUrl() : "N/A");
        profile.setBio(user.getBio() != null ? user.getBio() : "N/A");
        profile.setPhotoUrl(user.getPhotoUrl());

        return memberProfileRepository.save(profile);
    }

    public void updateAllProfilesForUser(com.curatix.vault.entity.UserEntity user) {
        List<MemberProfileEntity> profiles = memberProfileRepository.findAllByUserId(user.getId());
        for (MemberProfileEntity profile : profiles) {
            syncFromUser(profile.getBoard(), user);
        }
    }

    public List<MemberProfileEntity> getProfiles(String firebaseUid, Long boardId) {
        var user = userService.getByFirebaseUid(firebaseUid);
        permissionService.getRole(boardId, user.getId());
        return memberProfileRepository.findAllByBoardId(boardId);
    }

    @Transactional
    public MemberProfileEntity addProfile(String firebaseUid, Long boardId, MemberProfileRequest req) {
        var user = userService.getByFirebaseUid(firebaseUid);
        permissionService.requireEditorOrAbove(boardId, user.getId());

        BoardEntity board = boardRepository.findByIdAndDeletedFalse(boardId)
                .orElseThrow(() -> new ResourceNotFoundException("Board not found"));

        if (req.getFullName() == null || req.getFullName().isBlank()) {
            throw new BadRequestException("Full name is required");
        }

        MemberProfileEntity profile = MemberProfileEntity.builder()
                .board(board)
                .fullName(req.getFullName())
                .admissionNo(req.getAdmissionNo())
                .enrollmentNo(req.getEnrollmentNo())
                .email(req.getEmail())
                .phone(req.getPhone())
                .githubUrl(req.getGithubUrl())
                .linkedinUrl(req.getLinkedinUrl())
                .repoUrl(req.getRepoUrl())
                .roleInTeam(req.getRoleInTeam())
                .yearBranch(req.getYearBranch())
                .skills(req.getSkills())
                .bio(req.getBio())
                .build();

        return memberProfileRepository.save(profile);
    }

    @Transactional
    public MemberProfileEntity updateProfile(String firebaseUid, Long boardId, Long profileId,
            MemberProfileRequest req) {
        var user = userService.getByFirebaseUid(firebaseUid);
        permissionService.requireEditorOrAbove(boardId, user.getId());

        MemberProfileEntity profile = memberProfileRepository.findById(profileId)
                .filter(p -> p.getBoard().getId().equals(boardId))
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        if (req.getFullName() != null)
            profile.setFullName(req.getFullName());
        if (req.getAdmissionNo() != null)
            profile.setAdmissionNo(req.getAdmissionNo());
        if (req.getEnrollmentNo() != null)
            profile.setEnrollmentNo(req.getEnrollmentNo());
        if (req.getEmail() != null)
            profile.setEmail(req.getEmail());
        if (req.getPhone() != null)
            profile.setPhone(req.getPhone());
        if (req.getGithubUrl() != null)
            profile.setGithubUrl(req.getGithubUrl());
        if (req.getLinkedinUrl() != null)
            profile.setLinkedinUrl(req.getLinkedinUrl());
        if (req.getRepoUrl() != null)
            profile.setRepoUrl(req.getRepoUrl());
        if (req.getRoleInTeam() != null)
            profile.setRoleInTeam(req.getRoleInTeam());
        if (req.getYearBranch() != null)
            profile.setYearBranch(req.getYearBranch());
        if (req.getSkills() != null)
            profile.setSkills(req.getSkills());
        if (req.getBio() != null)
            profile.setBio(req.getBio());

        return memberProfileRepository.save(profile);
    }

    @Transactional
    public void deleteProfile(String firebaseUid, Long boardId, Long profileId) {
        var user = userService.getByFirebaseUid(firebaseUid);
        permissionService.requireEditorOrAbove(boardId, user.getId());

        MemberProfileEntity profile = memberProfileRepository.findById(profileId)
                .filter(p -> p.getBoard().getId().equals(boardId))
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        // Delete photo from Cloudinary if exists
        if (profile.getCloudinaryPublicId() != null) {
            try {
                cloudinary.uploader().destroy(profile.getCloudinaryPublicId(), ObjectUtils.emptyMap());
            } catch (IOException e) {
                // Log but don't fail delete
            }
        }

        memberProfileRepository.delete(profile);
    }

    @Transactional
    public MemberProfileEntity uploadProfilePhoto(String firebaseUid, Long boardId, Long profileId,
            MultipartFile file) throws IOException {
        var user = userService.getByFirebaseUid(firebaseUid);
        permissionService.requireEditorOrAbove(boardId, user.getId());

        MemberProfileEntity profile = memberProfileRepository.findById(profileId)
                .filter(p -> p.getBoard().getId().equals(boardId))
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        // Delete old photo if exists
        if (profile.getCloudinaryPublicId() != null) {
            cloudinary.uploader().destroy(profile.getCloudinaryPublicId(), ObjectUtils.emptyMap());
        }

        Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                "folder", "curatix/profiles",
                "transformation", "c_fill,w_400,h_400,g_face"));

        profile.setPhotoUrl((String) result.get("secure_url"));
        profile.setCloudinaryPublicId((String) result.get("public_id"));

        return memberProfileRepository.save(profile);
    }
}
