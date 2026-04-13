package com.curatix.vault.service;

import com.curatix.vault.entity.BoardEntity;
import com.curatix.vault.entity.BoardMemberEntity;
import com.curatix.vault.entity.InviteLinkEntity;
import com.curatix.vault.entity.UserEntity;
import com.curatix.vault.exception.BadRequestException;
import com.curatix.vault.exception.ResourceNotFoundException;
import com.curatix.vault.repository.BoardMemberRepository;
import com.curatix.vault.repository.BoardRepository;
import com.curatix.vault.repository.InviteLinkRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service class for managing shareable Board Invitation Links.
 * These links use UUID tokens to allow users to join a board without a direct email invite.
 * Supports expiration, usage limits, and role-based joining.
 */
@Service
@RequiredArgsConstructor
public class InviteService {

    private static final Logger log = LoggerFactory.getLogger(InviteService.class);

    private final InviteLinkRepository inviteLinkRepository;
    private final BoardRepository boardRepository;
    private final BoardMemberRepository boardMemberRepository;
    private final PermissionService permissionService;
    private final UserService userService;
    private final MemberProfileService memberProfileService;

    /**
     * Generates a new unique invite link for a board.
     * 
     * @param firebaseUid The unique identifier of the user creating the link.
     * @param boardId The ID of the board.
     * @param role The role to grant (EDITOR or VIEWER).
     * @param maxUses The maximum number of times this link can be used (null for unlimited).
     * @return The newly created InviteLinkEntity.
     */
    @Transactional
    public InviteLinkEntity generateInviteLink(String firebaseUid, Long boardId,
                                                String role, Integer maxUses) {
        UserEntity user = userService.getByFirebaseUid(firebaseUid);
        permissionService.requireEditorOrAbove(boardId, user.getId());

        BoardEntity board = boardRepository.findByIdAndDeletedFalse(boardId)
                .orElseThrow(() -> new ResourceNotFoundException("Board not found"));

        BoardMemberEntity.Role inviteRole;
        try {
            inviteRole = BoardMemberEntity.Role.valueOf(role.toUpperCase());
        } catch (Exception e) {
            throw new BadRequestException("Invalid role. Must be EDITOR or VIEWER");
        }

        if (inviteRole == BoardMemberEntity.Role.OWNER) {
            throw new BadRequestException("Cannot generate OWNER invite links");
        }

        InviteLinkEntity link = InviteLinkEntity.builder()
                .board(board)
                .token(UUID.randomUUID().toString().replace("-", ""))
                .role(inviteRole)
                .createdBy(user)
                .expiresAt(LocalDateTime.now().plusDays(7))
                .maxUses(maxUses)
                .useCount(0)
                .active(true)
                .build();

        log.info("User {} generated {} invite link for board {}", firebaseUid, inviteRole, boardId);
        return inviteLinkRepository.save(link);
    }

    /**
     * Retrieves all currently active and unexpired invite links for a specific board.
     * 
     * @param firebaseUid The unique identifier of the requesting user.
     * @param boardId The ID of the board.
     * @return A list of active InviteLinkEntity objects.
     */
    public List<InviteLinkEntity> getActiveLinks(String firebaseUid, Long boardId) {
        var user = userService.getByFirebaseUid(firebaseUid);
        permissionService.requireEditorOrAbove(boardId, user.getId());
        return inviteLinkRepository.findAllByBoardIdAndActiveTrue(boardId);
    }

    @Transactional
    public void revokeLink(String firebaseUid, Long boardId, Long linkId) {
        var user = userService.getByFirebaseUid(firebaseUid);
        permissionService.requireOwner(boardId, user.getId());

        InviteLinkEntity link = inviteLinkRepository.findById(linkId)
                .filter(l -> l.getBoard().getId().equals(boardId))
                .orElseThrow(() -> new ResourceNotFoundException("Invite link not found"));

        log.warn("User {} revoked invite link {} for board {}", firebaseUid, linkId, boardId);
        link.setActive(false);
        inviteLinkRepository.save(link);
    }

    /**
     * Allows a user to join a board using a valid invite token.
     * Validates that the link is active, not expired, and has usage capacity remaining.
     * 
     * @param firebaseUid The unique identifier of the user joining.
     * @param token The unique UUID-based token.
     * @return A map containing success message and board metadata.
     * @throws BadRequestException if the link is invalid, expired, or capped.
     */
    @Transactional
    public Map<String, Object> joinViaLink(String firebaseUid, String token) {
        UserEntity user = userService.getByFirebaseUid(firebaseUid);

        InviteLinkEntity link = inviteLinkRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Invite link not found or invalid"));

        if (!link.isActive()) {
            throw new BadRequestException("This invite link has been revoked");
        }
        if (link.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("This invite link has expired");
        }
        if (link.getMaxUses() != null && link.getUseCount() >= link.getMaxUses()) {
            throw new BadRequestException("This invite link has reached its maximum uses");
        }

        Long boardId = link.getBoard().getId();

        // Already a member? Just return success
        if (boardMemberRepository.existsByBoardIdAndUserId(boardId, user.getId())) {
            return Map.of(
                    "message", "You are already a member of this board",
                    "boardId", boardId,
                    "boardName", link.getBoard().getName()
            );
        }

        // Add to board
        BoardMemberEntity membership = BoardMemberEntity.builder()
                .board(link.getBoard())
                .user(user)
                .role(link.getRole())
                .build();
        boardMemberRepository.save(membership);

        // Sync/Create member profile from global user profile
        memberProfileService.syncFromUser(link.getBoard(), user);

        // Increment use count
        link.setUseCount(link.getUseCount() + 1);
        inviteLinkRepository.save(link);

        return Map.of(
                "message", "Successfully joined the board",
                "boardId", boardId,
                "boardName", link.getBoard().getName(),
                "role", link.getRole().name()
        );
    }

    /** Public endpoint — preview invite link info before joining */
    public Map<String, Object> previewLink(String token) {
        InviteLinkEntity link = inviteLinkRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Invite link not found or invalid"));

        if (!link.isActive() || link.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("This invite link is no longer valid");
        }

        return Map.of(
                "boardName", link.getBoard().getName(),
                "role",      link.getRole().name(),
                "expiresAt", link.getExpiresAt().toString()
        );
    }
}
