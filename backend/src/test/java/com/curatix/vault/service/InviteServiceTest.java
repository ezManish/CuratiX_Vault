package com.curatix.vault.service;

import com.curatix.vault.entity.*;
import com.curatix.vault.exception.BadRequestException;
import com.curatix.vault.exception.ResourceNotFoundException;
import com.curatix.vault.repository.BoardMemberRepository;
import com.curatix.vault.repository.BoardRepository;
import com.curatix.vault.repository.InviteLinkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("InviteService")
class InviteServiceTest {

    @Mock InviteLinkRepository    inviteLinkRepository;
    @Mock BoardRepository         boardRepository;
    @Mock BoardMemberRepository   boardMemberRepository;
    @Mock PermissionService       permissionService;
    @Mock UserService             userService;
    @Mock MemberProfileService    memberProfileService;

    @InjectMocks
    InviteService inviteService;

    /* ── fixtures ──────────────────────────────────────────────────── */
    private UserEntity owner;
    private UserEntity newUser;
    private BoardEntity board;

    @BeforeEach
    void setUp() {
        owner = UserEntity.builder().id(1L).firebaseUid("uid-owner")
                .email("owner@test.com").displayName("Owner").build();
        newUser = UserEntity.builder().id(3L).firebaseUid("uid-new")
                .email("new@test.com").displayName("New Guy").build();
        board = BoardEntity.builder().id(10L).name("Hack Board")
                .owner(owner).build();
    }

    private InviteLinkEntity validLink(boolean active, LocalDateTime expiresAt, Integer maxUses, int useCount) {
        return InviteLinkEntity.builder()
                .id(100L)
                .board(board)
                .token("abc123token")
                .role(BoardMemberEntity.Role.EDITOR)
                .createdBy(owner)
                .expiresAt(expiresAt)
                .maxUses(maxUses)
                .useCount(useCount)
                .active(active)
                .build();
    }

    // ================================================================ generateInviteLink
    @Nested
    @DisplayName("generateInviteLink()")
    class GenerateInviteLink {

        @Test
        @DisplayName("generates a link with 7-day expiry and correct role")
        void generatesLink() {
            when(userService.getByFirebaseUid("uid-owner")).thenReturn(owner);
            when(boardRepository.findByIdAndDeletedFalse(10L)).thenReturn(Optional.of(board));
            when(inviteLinkRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            InviteLinkEntity link = inviteService.generateInviteLink("uid-owner", 10L, "EDITOR", 5);

            assertThat(link.getRole()).isEqualTo(BoardMemberEntity.Role.EDITOR);
            assertThat(link.getMaxUses()).isEqualTo(5);
            assertThat(link.getExpiresAt()).isAfter(LocalDateTime.now().plusDays(6));
            assertThat(link.getToken()).isNotBlank().hasSize(32); // UUID without dashes
        }

        @Test
        @DisplayName("throws BadRequestException when role is OWNER")
        void throwsForOwnerRole() {
            when(userService.getByFirebaseUid("uid-owner")).thenReturn(owner);
            when(boardRepository.findByIdAndDeletedFalse(10L)).thenReturn(Optional.of(board));

            assertThatThrownBy(() -> inviteService.generateInviteLink("uid-owner", 10L, "OWNER", null))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessageContaining("Cannot generate OWNER invite links");
        }

        @Test
        @DisplayName("throws BadRequestException for an invalid role string")
        void throwsForInvalidRole() {
            when(userService.getByFirebaseUid("uid-owner")).thenReturn(owner);
            when(boardRepository.findByIdAndDeletedFalse(10L)).thenReturn(Optional.of(board));

            assertThatThrownBy(() -> inviteService.generateInviteLink("uid-owner", 10L, "SUPERUSER", null))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessageContaining("Invalid role");
        }
    }

    // ================================================================ joinViaLink
    @Nested
    @DisplayName("joinViaLink()")
    class JoinViaLink {

        @Test
        @DisplayName("adds membership and increments use count on valid link")
        void joinsSuccessfully() {
            InviteLinkEntity link = validLink(true, LocalDateTime.now().plusDays(5), null, 0);

            when(userService.getByFirebaseUid("uid-new")).thenReturn(newUser);
            when(inviteLinkRepository.findByToken("abc123token")).thenReturn(Optional.of(link));
            when(boardMemberRepository.existsByBoardIdAndUserId(10L, newUser.getId())).thenReturn(false);
            when(boardMemberRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
            when(inviteLinkRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Map<String, Object> result = inviteService.joinViaLink("uid-new", "abc123token");

            assertThat(result.get("message")).isEqualTo("Successfully joined the board");
            assertThat(link.getUseCount()).isEqualTo(1);
            verify(memberProfileService).syncFromUser(board, newUser);
        }

        @Test
        @DisplayName("returns already-member message without duplicate membership")
        void returnsAlreadyMemberMessage() {
            InviteLinkEntity link = validLink(true, LocalDateTime.now().plusDays(5), null, 0);

            when(userService.getByFirebaseUid("uid-new")).thenReturn(newUser);
            when(inviteLinkRepository.findByToken("abc123token")).thenReturn(Optional.of(link));
            when(boardMemberRepository.existsByBoardIdAndUserId(10L, newUser.getId())).thenReturn(true);

            Map<String, Object> result = inviteService.joinViaLink("uid-new", "abc123token");

            assertThat(result.get("message")).asString().contains("already a member");
            verify(boardMemberRepository, never()).save(any());
        }

        @Test
        @DisplayName("throws BadRequestException when link is revoked")
        void throwsForRevokedLink() {
            InviteLinkEntity link = validLink(false, LocalDateTime.now().plusDays(5), null, 0);

            when(userService.getByFirebaseUid("uid-new")).thenReturn(newUser);
            when(inviteLinkRepository.findByToken("abc123token")).thenReturn(Optional.of(link));

            assertThatThrownBy(() -> inviteService.joinViaLink("uid-new", "abc123token"))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessageContaining("revoked");
        }

        @Test
        @DisplayName("throws BadRequestException when link is expired")
        void throwsForExpiredLink() {
            InviteLinkEntity link = validLink(true, LocalDateTime.now().minusDays(1), null, 0);

            when(userService.getByFirebaseUid("uid-new")).thenReturn(newUser);
            when(inviteLinkRepository.findByToken("abc123token")).thenReturn(Optional.of(link));

            assertThatThrownBy(() -> inviteService.joinViaLink("uid-new", "abc123token"))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessageContaining("expired");
        }

        @Test
        @DisplayName("throws BadRequestException when max uses exceeded")
        void throwsWhenMaxUsesReached() {
            InviteLinkEntity link = validLink(true, LocalDateTime.now().plusDays(5), 3, 3);

            when(userService.getByFirebaseUid("uid-new")).thenReturn(newUser);
            when(inviteLinkRepository.findByToken("abc123token")).thenReturn(Optional.of(link));

            assertThatThrownBy(() -> inviteService.joinViaLink("uid-new", "abc123token"))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessageContaining("maximum uses");
        }
    }

    // ================================================================ previewLink
    @Nested
    @DisplayName("previewLink()")
    class PreviewLink {

        @Test
        @DisplayName("returns board name, role and expiry for a valid link")
        void returnsPreviewData() {
            InviteLinkEntity link = validLink(true, LocalDateTime.now().plusDays(3), null, 0);

            when(inviteLinkRepository.findByToken("abc123token")).thenReturn(Optional.of(link));

            Map<String, Object> preview = inviteService.previewLink("abc123token");

            assertThat(preview).containsKey("boardName")
                                .containsKey("role")
                                .containsKey("expiresAt");
        }

        @Test
        @DisplayName("throws BadRequestException for revoked link preview")
        void throwsForRevokedLinkPreview() {
            InviteLinkEntity link = validLink(false, LocalDateTime.now().plusDays(3), null, 0);

            when(inviteLinkRepository.findByToken("abc123token")).thenReturn(Optional.of(link));

            assertThatThrownBy(() -> inviteService.previewLink("abc123token"))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessageContaining("no longer valid");
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when token doesn't exist")
        void throwsForUnknownToken() {
            when(inviteLinkRepository.findByToken("bad-token")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> inviteService.previewLink("bad-token"))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    // ================================================================ revokeLink
    @Nested
    @DisplayName("revokeLink()")
    class RevokeLink {

        @Test
        @DisplayName("sets active=false on the link")
        void revokesLink() {
            InviteLinkEntity link = validLink(true, LocalDateTime.now().plusDays(5), null, 0);

            when(userService.getByFirebaseUid("uid-owner")).thenReturn(owner);
            when(inviteLinkRepository.findById(100L)).thenReturn(Optional.of(link));
            when(inviteLinkRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            inviteService.revokeLink("uid-owner", 10L, 100L);

            assertThat(link.isActive()).isFalse();
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when link doesn't belong to board")
        void throwsWhenLinkNotOnBoard() {
            // Link belongs to a different board (id=99)
            BoardEntity otherBoard = BoardEntity.builder().id(99L).name("Other").build();
            InviteLinkEntity link = InviteLinkEntity.builder()
                    .id(100L).board(otherBoard).active(true)
                    .expiresAt(LocalDateTime.now().plusDays(1)).build();

            when(userService.getByFirebaseUid("uid-owner")).thenReturn(owner);
            when(inviteLinkRepository.findById(100L)).thenReturn(Optional.of(link));

            assertThatThrownBy(() -> inviteService.revokeLink("uid-owner", 10L, 100L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }
}
