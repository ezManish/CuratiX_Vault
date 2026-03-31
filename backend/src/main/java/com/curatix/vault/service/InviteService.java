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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InviteService {

    private final InviteLinkRepository inviteLinkRepository;
    private final BoardRepository boardRepository;
    private final BoardMemberRepository boardMemberRepository;
    private final PermissionService permissionService;
    private final UserService userService;
    private final MemberProfileService memberProfileService;

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

        return inviteLinkRepository.save(link);
    }

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

        link.setActive(false);
        inviteLinkRepository.save(link);
    }

    /**
     * Join a board via invite link token. Does NOT require prior board membership.
     * Token is validated: active, not expired, usage cap not exceeded.
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
