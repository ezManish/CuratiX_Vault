package com.curatix.vault.service;

import com.curatix.vault.entity.BoardMemberEntity;
import com.curatix.vault.exception.AccessDeniedException;
import com.curatix.vault.repository.BoardMemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("PermissionService")
class PermissionServiceTest {

    @Mock
    BoardMemberRepository boardMemberRepository;

    @InjectMocks
    PermissionService permissionService;

    private static final Long BOARD_ID = 1L;
    private static final Long USER_ID  = 10L;

    private BoardMemberEntity memberWithRole(BoardMemberEntity.Role role) {
        return BoardMemberEntity.builder()
                .board(null)
                .user(null)
                .role(role)
                .build();
    }

    // ------------------------------------------------------------------ getRole
    @Nested
    @DisplayName("getRole()")
    class GetRole {

        @Test
        @DisplayName("returns role when member exists")
        void returnsRole() {
            when(boardMemberRepository.findByBoardIdAndUserId(BOARD_ID, USER_ID))
                    .thenReturn(Optional.of(memberWithRole(BoardMemberEntity.Role.EDITOR)));

            BoardMemberEntity.Role role = permissionService.getRole(BOARD_ID, USER_ID);

            assertThat(role).isEqualTo(BoardMemberEntity.Role.EDITOR);
        }

        @Test
        @DisplayName("throws AccessDeniedException when not a member")
        void throwsWhenNotMember() {
            when(boardMemberRepository.findByBoardIdAndUserId(BOARD_ID, USER_ID))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> permissionService.getRole(BOARD_ID, USER_ID))
                    .isInstanceOf(AccessDeniedException.class)
                    .hasMessageContaining("not a member");
        }
    }

    // ------------------------------------------------------------- requireOwner
    @Nested
    @DisplayName("requireOwner()")
    class RequireOwner {

        @Test
        @DisplayName("passes silently for OWNER")
        void passesForOwner() {
            when(boardMemberRepository.findByBoardIdAndUserId(BOARD_ID, USER_ID))
                    .thenReturn(Optional.of(memberWithRole(BoardMemberEntity.Role.OWNER)));

            // no exception expected
            permissionService.requireOwner(BOARD_ID, USER_ID);
        }

        @Test
        @DisplayName("throws for EDITOR")
        void throwsForEditor() {
            when(boardMemberRepository.findByBoardIdAndUserId(BOARD_ID, USER_ID))
                    .thenReturn(Optional.of(memberWithRole(BoardMemberEntity.Role.EDITOR)));

            assertThatThrownBy(() -> permissionService.requireOwner(BOARD_ID, USER_ID))
                    .isInstanceOf(AccessDeniedException.class)
                    .hasMessageContaining("Only the Board Owner");
        }

        @Test
        @DisplayName("throws for VIEWER")
        void throwsForViewer() {
            when(boardMemberRepository.findByBoardIdAndUserId(BOARD_ID, USER_ID))
                    .thenReturn(Optional.of(memberWithRole(BoardMemberEntity.Role.VIEWER)));

            assertThatThrownBy(() -> permissionService.requireOwner(BOARD_ID, USER_ID))
                    .isInstanceOf(AccessDeniedException.class);
        }
    }

    // ------------------------------------------------------- requireEditorOrAbove
    @Nested
    @DisplayName("requireEditorOrAbove()")
    class RequireEditorOrAbove {

        @Test
        @DisplayName("passes for OWNER")
        void passesForOwner() {
            when(boardMemberRepository.findByBoardIdAndUserId(BOARD_ID, USER_ID))
                    .thenReturn(Optional.of(memberWithRole(BoardMemberEntity.Role.OWNER)));

            permissionService.requireEditorOrAbove(BOARD_ID, USER_ID);
        }

        @Test
        @DisplayName("passes for EDITOR")
        void passesForEditor() {
            when(boardMemberRepository.findByBoardIdAndUserId(BOARD_ID, USER_ID))
                    .thenReturn(Optional.of(memberWithRole(BoardMemberEntity.Role.EDITOR)));

            permissionService.requireEditorOrAbove(BOARD_ID, USER_ID);
        }

        @Test
        @DisplayName("throws for VIEWER")
        void throwsForViewer() {
            when(boardMemberRepository.findByBoardIdAndUserId(BOARD_ID, USER_ID))
                    .thenReturn(Optional.of(memberWithRole(BoardMemberEntity.Role.VIEWER)));

            assertThatThrownBy(() -> permissionService.requireEditorOrAbove(BOARD_ID, USER_ID))
                    .isInstanceOf(AccessDeniedException.class)
                    .hasMessageContaining("Editors or Owners");
        }
    }

    // --------------------------------------------------------------- isMember
    @Nested
    @DisplayName("isMember()")
    class IsMember {

        @Test
        @DisplayName("returns true when member exists")
        void trueWhenExists() {
            when(boardMemberRepository.existsByBoardIdAndUserId(BOARD_ID, USER_ID)).thenReturn(true);
            assertThat(permissionService.isMember(BOARD_ID, USER_ID)).isTrue();
        }

        @Test
        @DisplayName("returns false when not a member")
        void falseWhenNotExists() {
            when(boardMemberRepository.existsByBoardIdAndUserId(BOARD_ID, USER_ID)).thenReturn(false);
            assertThat(permissionService.isMember(BOARD_ID, USER_ID)).isFalse();
        }
    }
}
