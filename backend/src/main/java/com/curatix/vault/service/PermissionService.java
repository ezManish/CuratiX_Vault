package com.curatix.vault.service;

import com.curatix.vault.entity.BoardMemberEntity;
import com.curatix.vault.exception.AccessDeniedException;
import com.curatix.vault.repository.BoardMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final BoardMemberRepository boardMemberRepository;

    /**
     * Returns the current user's role in the board, or throws if not a member.
     */
    public BoardMemberEntity.Role getRole(Long boardId, Long userId) {
        return boardMemberRepository.findByBoardIdAndUserId(boardId, userId)
                .map(BoardMemberEntity::getRole)
                .orElseThrow(() -> new AccessDeniedException("You are not a member of this board"));
    }

    /**
     * Ensures user is OWNER or EDITOR — throws 403 otherwise.
     */
    public void requireEditorOrAbove(Long boardId, Long userId) {
        BoardMemberEntity.Role role = getRole(boardId, userId);
        if (role == BoardMemberEntity.Role.VIEWER) {
            throw new AccessDeniedException("Editors or Owners can perform this action");
        }
    }

    /**
     * Ensures user is OWNER — throws 403 otherwise.
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
