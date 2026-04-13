package com.curatix.vault.service;

import com.curatix.vault.dto.CreateBoardRequest;
import com.curatix.vault.entity.*;
import com.curatix.vault.exception.AccessDeniedException;
import com.curatix.vault.exception.BadRequestException;
import com.curatix.vault.exception.ResourceNotFoundException;
import com.curatix.vault.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BoardService")
class BoardServiceTest {

    @Mock BoardRepository             boardRepository;
    @Mock BoardMemberRepository       boardMemberRepository;
    @Mock BoardInvitationRepository   boardInvitationRepository;
    @Mock MemberProfileRepository     memberProfileRepository;
    @Mock PermissionService           permissionService;
    @Mock UserService                 userService;
    @Mock MemberProfileService        memberProfileService;

    @InjectMocks
    BoardService boardService;

    /* ── shared fixtures ───────────────────────────────────────────── */
    private UserEntity owner;
    private UserEntity editor;
    private BoardEntity board;

    @BeforeEach
    void setUp() {
        owner = UserEntity.builder().id(1L).firebaseUid("uid-owner")
                .email("owner@test.com").displayName("Owner").build();
        editor = UserEntity.builder().id(2L).firebaseUid("uid-editor")
                .email("editor@test.com").displayName("Editor").build();
        board = BoardEntity.builder().id(10L).name("Test Board")
                .owner(owner).deleted(false).build();
    }

    /* ── helpers ───────────────────────────────────────────────────── */
    private CreateBoardRequest basicRequest(String name) {
        CreateBoardRequest req = new CreateBoardRequest();
        req.setName(name);
        return req;
    }

    // ================================================================ createBoard
    @Nested
    @DisplayName("createBoard()")
    class CreateBoard {

        @Test
        @DisplayName("saves board, adds OWNER membership, syncs profile")
        void createsBoardSuccessfully() {
            when(userService.getByFirebaseUid("uid-owner")).thenReturn(owner);
            when(boardRepository.save(any())).thenAnswer(inv -> {
                BoardEntity b = inv.getArgument(0);
                b = BoardEntity.builder().id(99L).name(b.getName()).owner(b.getOwner()).deleted(false).build();
                return b;
            });

            CreateBoardRequest req = basicRequest("My Hackathon");
            req.setCoverColor("#ff0000");

            BoardEntity result = boardService.createBoard("uid-owner", req);

            assertThat(result.getName()).isEqualTo("My Hackathon");

            // OWNER membership must be created
            ArgumentCaptor<BoardMemberEntity> memberCaptor =
                    ArgumentCaptor.forClass(BoardMemberEntity.class);
            verify(boardMemberRepository).save(memberCaptor.capture());
            assertThat(memberCaptor.getValue().getRole()).isEqualTo(BoardMemberEntity.Role.OWNER);

            // Profile must be synced
            verify(memberProfileService).syncFromUser(any(BoardEntity.class), eq(owner));
        }

        @Test
        @DisplayName("uses default cover color when none provided")
        void usesDefaultCoverColor() {
            when(userService.getByFirebaseUid("uid-owner")).thenReturn(owner);
            when(boardRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            BoardEntity result = boardService.createBoard("uid-owner", basicRequest("Board"));

            assertThat(result.getCoverColor()).isEqualTo("#6366f1");
        }

        @Test
        @DisplayName("uses default result PARTICIPATED when none provided")
        void usesDefaultResult() {
            when(userService.getByFirebaseUid("uid-owner")).thenReturn(owner);
            when(boardRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            BoardEntity result = boardService.createBoard("uid-owner", basicRequest("Board"));

            assertThat(result.getResult()).isEqualTo(Result.PARTICIPATED);
        }
    }

    // ================================================================ getBoard
    @Nested
    @DisplayName("getBoard()")
    class GetBoard {

        @Test
        @DisplayName("returns board when user is a member")
        void returnsBoardForMember() {
            when(userService.getByFirebaseUid("uid-owner")).thenReturn(owner);
            when(permissionService.getRole(10L, 1L)).thenReturn(BoardMemberEntity.Role.OWNER);
            when(boardRepository.findByIdAndDeletedFalse(10L)).thenReturn(Optional.of(board));

            BoardEntity result = boardService.getBoard("uid-owner", 10L);

            assertThat(result.getId()).isEqualTo(10L);
        }

        @Test
        @DisplayName("throws ResourceNotFoundException for deleted / missing board")
        void throwsForMissingBoard() {
            when(userService.getByFirebaseUid("uid-owner")).thenReturn(owner);
            when(permissionService.getRole(10L, 1L)).thenReturn(BoardMemberEntity.Role.OWNER);
            when(boardRepository.findByIdAndDeletedFalse(10L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> boardService.getBoard("uid-owner", 10L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Board not found");
        }
    }

    // ================================================================ updateBoard
    @Nested
    @DisplayName("updateBoard()")
    class UpdateBoard {

        @Test
        @DisplayName("editor can update board fields")
        void editorCanUpdate() {
            when(userService.getByFirebaseUid("uid-editor")).thenReturn(editor);
            // requireEditorOrAbove passes (no exception)
            when(boardRepository.findByIdAndDeletedFalse(10L)).thenReturn(Optional.of(board));
            when(boardRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            CreateBoardRequest req = basicRequest("Updated Name");
            req.setVenue("MIT");
            req.setRepoUrls(List.of("https://github.com/test/repo"));

            BoardEntity result = boardService.updateBoard("uid-editor", 10L, req);

            assertThat(result.getName()).isEqualTo("Updated Name");
            assertThat(result.getVenue()).isEqualTo("MIT");
            assertThat(result.getRepoUrls()).containsExactly("https://github.com/test/repo");
        }

        @Test
        @DisplayName("clears repoUrls when null list is passed")
        void clearsRepoUrlsWhenNull() {
            when(userService.getByFirebaseUid("uid-owner")).thenReturn(owner);
            when(boardRepository.findByIdAndDeletedFalse(10L)).thenReturn(Optional.of(board));
            when(boardRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            CreateBoardRequest req = basicRequest("Board");
            req.setRepoUrls(null);

            BoardEntity result = boardService.updateBoard("uid-owner", 10L, req);

            assertThat(result.getRepoUrls()).isNotNull().isEmpty();
        }
    }

    // ================================================================ deleteBoard
    @Nested
    @DisplayName("deleteBoard()")
    class DeleteBoard {

        @Test
        @DisplayName("soft-deletes the board")
        void softDeletesBoard() {
            when(userService.getByFirebaseUid("uid-owner")).thenReturn(owner);
            when(boardRepository.findByIdAndDeletedFalse(10L)).thenReturn(Optional.of(board));

            boardService.deleteBoard("uid-owner", 10L);

            ArgumentCaptor<BoardEntity> captor = ArgumentCaptor.forClass(BoardEntity.class);
            verify(boardRepository).save(captor.capture());
            assertThat(captor.getValue().isDeleted()).isTrue();
        }

        @Test
        @DisplayName("throws when editor tries to delete (owner-only)")
        void editorCannotDelete() {
            when(userService.getByFirebaseUid("uid-editor")).thenReturn(editor);
            doThrow(new AccessDeniedException("Only the Board Owner can perform this action"))
                    .when(permissionService).requireOwner(10L, editor.getId());

            assertThatThrownBy(() -> boardService.deleteBoard("uid-editor", 10L))
                    .isInstanceOf(AccessDeniedException.class)
                    .hasMessageContaining("Only the Board Owner");
        }
    }

    // ================================================================ addMemberByEmail
    @Nested
    @DisplayName("addMemberByEmail()")
    class AddMemberByEmail {

        @Test
        @DisplayName("creates a PENDING invitation for a new email")
        void createsInvitationForNewEmail() {
            when(userService.getByFirebaseUid("uid-owner")).thenReturn(owner);
            when(boardMemberRepository.existsByBoardIdAndUserEmail(10L, "new@test.com"))
                    .thenReturn(false);
            when(boardInvitationRepository.findByBoardIdAndRecipientEmailIgnoreCase(10L, "new@test.com"))
                    .thenReturn(Optional.empty());
            when(boardRepository.findById(10L)).thenReturn(Optional.of(board));

            boardService.addMemberByEmail("uid-owner", 10L, "new@test.com",
                    BoardMemberEntity.Role.EDITOR);

            ArgumentCaptor<BoardInvitationEntity> captor =
                    ArgumentCaptor.forClass(BoardInvitationEntity.class);
            verify(boardInvitationRepository).save(captor.capture());
            assertThat(captor.getValue().getRecipientEmail()).isEqualTo("new@test.com");
            assertThat(captor.getValue().getStatus()).isEqualTo(BoardInvitationEntity.Status.PENDING);
        }

        @Test
        @DisplayName("throws BadRequestException if user is already a member")
        void throwsIfAlreadyMember() {
            when(userService.getByFirebaseUid("uid-owner")).thenReturn(owner);
            when(boardMemberRepository.existsByBoardIdAndUserEmail(10L, "member@test.com"))
                    .thenReturn(true);

            assertThatThrownBy(() -> boardService.addMemberByEmail(
                    "uid-owner", 10L, "member@test.com", BoardMemberEntity.Role.VIEWER))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessageContaining("already a member");
        }

        @Test
        @DisplayName("throws BadRequestException if PENDING invitation already exists")
        void throwsIfPendingInviteExists() {
            when(userService.getByFirebaseUid("uid-owner")).thenReturn(owner);
            when(boardMemberRepository.existsByBoardIdAndUserEmail(10L, "pending@test.com"))
                    .thenReturn(false);

            BoardInvitationEntity existing = BoardInvitationEntity.builder()
                    .recipientEmail("pending@test.com")
                    .status(BoardInvitationEntity.Status.PENDING)
                    .build();
            when(boardInvitationRepository.findByBoardIdAndRecipientEmailIgnoreCase(10L, "pending@test.com"))
                    .thenReturn(Optional.of(existing));

            assertThatThrownBy(() -> boardService.addMemberByEmail(
                    "uid-owner", 10L, "pending@test.com", BoardMemberEntity.Role.EDITOR))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessageContaining("already pending");
        }
    }

    // ================================================================ respondToInvitation
    @Nested
    @DisplayName("respondToInvitation()")
    class RespondToInvitation {

        @Test
        @DisplayName("accepting creates membership and syncs profile")
        void acceptCreatessMembership() {
            BoardInvitationEntity invite = BoardInvitationEntity.builder()
                    .id(50L)
                    .board(board)
                    .recipientEmail(editor.getEmail())
                    .status(BoardInvitationEntity.Status.PENDING)
                    .role(BoardMemberEntity.Role.EDITOR)
                    .build();

            when(userService.getByFirebaseUid("uid-editor")).thenReturn(editor);
            when(boardInvitationRepository.findById(50L)).thenReturn(Optional.of(invite));

            boardService.respondToInvitation("uid-editor", 50L, true);

            verify(boardMemberRepository).save(any(BoardMemberEntity.class));
            verify(memberProfileService).syncFromUser(board, editor);
            assertThat(invite.getStatus()).isEqualTo(BoardInvitationEntity.Status.ACCEPTED);
        }

        @Test
        @DisplayName("declining only updates status — no membership created")
        void declineOnlyUpdatesStatus() {
            BoardInvitationEntity invite = BoardInvitationEntity.builder()
                    .id(51L)
                    .board(board)
                    .recipientEmail(editor.getEmail())
                    .status(BoardInvitationEntity.Status.PENDING)
                    .role(BoardMemberEntity.Role.VIEWER)
                    .build();

            when(userService.getByFirebaseUid("uid-editor")).thenReturn(editor);
            when(boardInvitationRepository.findById(51L)).thenReturn(Optional.of(invite));

            boardService.respondToInvitation("uid-editor", 51L, false);

            verify(boardMemberRepository, never()).save(any());
            verify(memberProfileService, never()).syncFromUser(any(), any());
            assertThat(invite.getStatus()).isEqualTo(BoardInvitationEntity.Status.DECLINED);
        }

        @Test
        @DisplayName("throws BadRequestException when invitation is not for this user")
        void throwsWhenInviteNotForUser() {
            BoardInvitationEntity invite = BoardInvitationEntity.builder()
                    .id(52L)
                    .board(board)
                    .recipientEmail("someone-else@test.com")
                    .status(BoardInvitationEntity.Status.PENDING)
                    .build();

            when(userService.getByFirebaseUid("uid-editor")).thenReturn(editor);
            when(boardInvitationRepository.findById(52L)).thenReturn(Optional.of(invite));

            assertThatThrownBy(() -> boardService.respondToInvitation("uid-editor", 52L, true))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessageContaining("not for you");
        }

        @Test
        @DisplayName("throws BadRequestException when invitation already processed")
        void throwsWhenAlreadyProcessed() {
            BoardInvitationEntity invite = BoardInvitationEntity.builder()
                    .id(53L)
                    .board(board)
                    .recipientEmail(editor.getEmail())
                    .status(BoardInvitationEntity.Status.ACCEPTED) // already done
                    .build();

            when(userService.getByFirebaseUid("uid-editor")).thenReturn(editor);
            when(boardInvitationRepository.findById(53L)).thenReturn(Optional.of(invite));

            assertThatThrownBy(() -> boardService.respondToInvitation("uid-editor", 53L, true))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessageContaining("already processed");
        }
    }

    // ================================================================ removeMember
    @Nested
    @DisplayName("removeMember()")
    class RemoveMember {

        @Test
        @DisplayName("throws BadRequestException when owner tries to remove themselves")
        void ownerCannotRemoveSelf() {
            when(userService.getByFirebaseUid("uid-owner")).thenReturn(owner);

            assertThatThrownBy(() -> boardService.removeMember("uid-owner", 10L, owner.getId()))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessageContaining("Owner cannot remove themselves");
        }

        @Test
        @DisplayName("removes membership and cleans up profile and invite")
        void removesMemberSuccessfully() {
            BoardMemberEntity membership = BoardMemberEntity.builder()
                    .id(20L).board(board).user(editor).role(BoardMemberEntity.Role.EDITOR).build();

            when(userService.getByFirebaseUid("uid-owner")).thenReturn(owner);
            when(boardMemberRepository.findByBoardIdAndUserId(10L, editor.getId()))
                    .thenReturn(Optional.of(membership));
            when(memberProfileRepository.findByBoardIdAndUserId(10L, editor.getId()))
                    .thenReturn(Optional.empty());
            when(boardInvitationRepository.findByBoardIdAndRecipientEmailIgnoreCase(10L, editor.getEmail()))
                    .thenReturn(Optional.empty());

            boardService.removeMember("uid-owner", 10L, editor.getId());

            verify(boardMemberRepository).delete(membership);
        }

        @Test
        @DisplayName("throws BadRequestException when trying to remove the board OWNER")
        void cannotRemoveOwner() {
            BoardMemberEntity ownerMembership = BoardMemberEntity.builder()
                    .id(21L).board(board).user(owner).role(BoardMemberEntity.Role.OWNER).build();

            when(userService.getByFirebaseUid("uid-editor")).thenReturn(editor);
            when(boardMemberRepository.findByBoardIdAndUserId(10L, owner.getId()))
                    .thenReturn(Optional.of(ownerMembership));

            assertThatThrownBy(() -> boardService.removeMember("uid-editor", 10L, owner.getId()))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessageContaining("Cannot remove the board owner");
        }
    }
}
