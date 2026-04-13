package com.curatix.vault.service;

import com.curatix.vault.entity.BoardMemberEntity;
import com.curatix.vault.exception.AccessDeniedException;
import com.curatix.vault.repository.BoardMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class for Role-Based Access Control (RBAC) within boards.
 * Defines the security hierarchy:
 * - OWNER: Full control (Delete board, manage members, edit settings)
 * - EDITOR: Collaborative control (Upload files, edit project details)
 * - VIEWER: Read-only access
 */
@Service
@RequiredArgsConstructor
public class PermissionService {

    private final BoardMemberRepository boardMemberRepository;

    /**
     * Retrieves the role of a user in a specific board.
     * 
     * @param boardId The ID of the board.
     * @param userId The ID of the user.
     * @return The User's role (OWNER, EDITOR, or VIEWER).
     * @throws AccessDeniedException if the user is not a member of the board.
     */
    public BoardMemberEntity.Role getRole(Long boardId, Long userId) {
        return boardMemberRepository.findByBoardIdAndUserId(boardId, userId)
                .map(BoardMemberEntity::getRole)
                .orElseThrow(() -> new AccessDeniedException("You are not a member of this board"));
    }

    /**
     * Ensures the user has EDITOR permissions or higher.
     * 
     * @param boardId The ID of the board.
     * @param userId The ID of the user.
     * @throws AccessDeniedException if the user has only VIEWER access or is not a member.
     */
    public void requireEditorOrAbove(Long boardId, Long userId) {
        BoardMemberEntity.Role role = getRole(boardId, userId);
        if (role == BoardMemberEntity.Role.VIEWER) {
            throw new AccessDeniedException("Editors or Owners can perform this action");
        }
    }

    /**
     * Ensures the user is the OWNER of the board.
     * 
     * @param boardId The ID of the board.
     * @param userId The ID of the user.
     * @throws AccessDeniedException if the user is not the board owner.
     */
    public void requireOwner(Long boardId, Long userId) {
        BoardMemberEntity.Role role = getRole(boardId, userId);
        if (role != BoardMemberEntity.Role.OWNER) {
            throw new AccessDeniedException("Only the Board Owner can perform this action");
        }
    }

    /**
     * Checks board membership without throwing.
     */
    public boolean isMember(Long boardId, Long userId) {
        return boardMemberRepository.existsByBoardIdAndUserId(boardId, userId);
    }
}
