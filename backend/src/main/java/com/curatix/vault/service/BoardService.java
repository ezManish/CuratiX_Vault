package com.curatix.vault.service;

import com.curatix.vault.dto.BoardMemberResponse;
import com.curatix.vault.dto.CreateBoardRequest;
import com.curatix.vault.entity.BoardEntity;
import com.curatix.vault.entity.BoardMemberEntity;
import com.curatix.vault.entity.MemberProfileEntity;
import com.curatix.vault.entity.UserEntity;
import com.curatix.vault.entity.BoardInvitationEntity;
import com.curatix.vault.exception.ResourceNotFoundException;
import com.curatix.vault.repository.BoardInvitationRepository;
import com.curatix.vault.repository.BoardMemberRepository;
import com.curatix.vault.repository.BoardRepository;
import com.curatix.vault.repository.MemberProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardMemberRepository boardMemberRepository;
    private final BoardInvitationRepository boardInvitationRepository;
    private final MemberProfileRepository memberProfileRepository;
    private final PermissionService permissionService;
    private final UserService userService;
    private final MemberProfileService memberProfileService;

    public List<BoardEntity> getBoardsForUser(String firebaseUid) {
        UserEntity user = userService.getByFirebaseUid(firebaseUid);
        return boardRepository.findAllByMemberUserId(user.getId());
    }

    @Transactional
    public BoardEntity createBoard(String firebaseUid, CreateBoardRequest req) {
        UserEntity owner = userService.getByFirebaseUid(firebaseUid);

        BoardEntity board = BoardEntity.builder()
                .name(req.getName())
                .description(req.getDescription())
                .coverColor(req.getCoverColor() != null ? req.getCoverColor() : "#6366f1")
                .coverEmoji(req.getCoverEmoji())
                .platform(req.getPlatform())
                .eventDate(req.getEventDate())
                .venue(req.getVenue())
                .theme(req.getTheme())
                .teamName(req.getTeamName())
                .problemStatement(req.getProblemStatement())
                .projectIdea(req.getProjectIdea())
                .result(req.getResult() != null ? req.getResult() : BoardEntity.Result.PARTICIPATED)
                .prize(req.getPrize())
                .submissionUrl(req.getSubmissionUrl())
                .repoUrl(req.getRepoUrl())
                .notes(req.getNotes())
                .owner(owner)
                .build();

        board = boardRepository.save(board);

        // Auto-add creator as OWNER in board_members
        BoardMemberEntity membership = BoardMemberEntity.builder()
                .board(board)
                .user(owner)
                .role(BoardMemberEntity.Role.OWNER)
                .build();
        boardMemberRepository.save(membership);

        // Auto-create board-specific profile for the owner from global profile
        memberProfileService.syncFromUser(board, owner);

        return board;
    }

    public BoardEntity getBoard(String firebaseUid, Long boardId) {
        UserEntity user = userService.getByFirebaseUid(firebaseUid);
        permissionService.getRole(boardId, user.getId()); // Throws if not member
        return boardRepository.findByIdAndDeletedFalse(boardId)
                .orElseThrow(() -> new ResourceNotFoundException("Board not found: " + boardId));
    }

    @Transactional
    public BoardEntity updateBoard(String firebaseUid, Long boardId, CreateBoardRequest req) {
        UserEntity user = userService.getByFirebaseUid(firebaseUid);
        permissionService.requireOwner(boardId, user.getId());

        BoardEntity board = boardRepository.findByIdAndDeletedFalse(boardId)
                .orElseThrow(() -> new ResourceNotFoundException("Board not found: " + boardId));

        board.setName(req.getName());
        if (req.getDescription() != null) board.setDescription(req.getDescription());
        if (req.getCoverColor() != null) board.setCoverColor(req.getCoverColor());
        if (req.getCoverEmoji() != null) board.setCoverEmoji(req.getCoverEmoji());
        
        // Update new fields
        board.setPlatform(req.getPlatform());
        board.setEventDate(req.getEventDate());
        board.setVenue(req.getVenue());
        board.setTheme(req.getTheme());
        board.setTeamName(req.getTeamName());
        board.setProblemStatement(req.getProblemStatement());
        board.setProjectIdea(req.getProjectIdea());
        if (req.getResult() != null) board.setResult(req.getResult());
        board.setPrize(req.getPrize());
        board.setSubmissionUrl(req.getSubmissionUrl());
        board.setRepoUrl(req.getRepoUrl());
        board.setNotes(req.getNotes());

        return boardRepository.save(board);
    }

    @Transactional
    public void deleteBoard(String firebaseUid, Long boardId) {
        UserEntity user = userService.getByFirebaseUid(firebaseUid);
        permissionService.requireOwner(boardId, user.getId());

        BoardEntity board = boardRepository.findByIdAndDeletedFalse(boardId)
                .orElseThrow(() -> new ResourceNotFoundException("Board not found: " + boardId));

        board.setDeleted(true);
        boardRepository.save(board);
    }

    public List<BoardMemberResponse> getBoardMembers(String firebaseUid, Long boardId) {
        UserEntity user = userService.getByFirebaseUid(firebaseUid);
        permissionService.getRole(boardId, user.getId());
        
        // 1. Fetch all members + users in ONE query (JOIN FETCH)
        List<BoardMemberEntity> members = boardMemberRepository.findAllByBoardIdJoinUser(boardId);
        
        // 2. Fetch all profiles for these users in ONE batch query
        List<Long> userIds = members.stream()
                .map(m -> m.getUser().getId())
                .collect(Collectors.toList());
        
        List<MemberProfileEntity> profiles = memberProfileRepository.findAllByBoardIdAndUserIdIn(boardId, userIds);
        
        // 3. Map profiles by UserId for O(1) lookup
        Map<Long, MemberProfileEntity> profileMap = profiles.stream()
                .filter(p -> p.getUser() != null)
                .collect(Collectors.toMap(p -> p.getUser().getId(), p -> p, (a, b) -> a));
        
        // 4. Build responses
        return members.stream().map(m -> BoardMemberResponse.builder()
                .id(m.getId())
                .user(m.getUser())
                .role(m.getRole())
                .profile(profileMap.get(m.getUser().getId()))
                .build()).collect(Collectors.toList());
    }

    @Transactional
    public void changeMemberRole(String firebaseUid, Long boardId, Long targetUserId,
                                  BoardMemberEntity.Role newRole) {
        UserEntity caller = userService.getByFirebaseUid(firebaseUid);
        permissionService.requireOwner(boardId, caller.getId());

        BoardMemberEntity bm = boardMemberRepository
                .findByBoardIdAndUserId(boardId, targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found in board"));

        if (bm.getRole() == BoardMemberEntity.Role.OWNER && newRole != BoardMemberEntity.Role.OWNER) {
            throw new com.curatix.vault.exception.BadRequestException(
                    "Cannot demote the board owner. Transfer ownership first.");
        }

        bm.setRole(newRole);
        boardMemberRepository.save(bm);
    }

    @Transactional
    public void removeMember(String firebaseUid, Long boardId, Long targetUserId) {
        UserEntity caller = userService.getByFirebaseUid(firebaseUid);
        permissionService.requireOwner(boardId, caller.getId());

        if (caller.getId().equals(targetUserId)) {
            throw new com.curatix.vault.exception.BadRequestException("Owner cannot remove themselves from the board");
        }

        BoardMemberEntity bm = boardMemberRepository
                .findByBoardIdAndUserId(boardId, targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found in board"));

        if (bm.getRole() == BoardMemberEntity.Role.OWNER) {
            throw new com.curatix.vault.exception.BadRequestException("Cannot remove the board owner");
        }

        // 1. Remove board-specific profile record first
        memberProfileRepository.findByBoardIdAndUserId(boardId, targetUserId)
                .ifPresent(memberProfileRepository::delete);

        // 2. Remove the membership record
        boardMemberRepository.delete(bm);
        
        // 3. Clear any pending invitations for this email (cleanup)
        if (bm.getUser() != null) {
            boardInvitationRepository.findByBoardIdAndRecipientEmailIgnoreCase(boardId, bm.getUser().getEmail())
                    .ifPresent(boardInvitationRepository::delete);
        }
    }

    @Transactional
    public void addMemberByEmail(String firebaseUid, Long boardId, String email, BoardMemberEntity.Role role) {
        UserEntity caller = userService.getByFirebaseUid(firebaseUid);
        permissionService.requireOwner(boardId, caller.getId());

        if (boardMemberRepository.existsByBoardIdAndUserEmail(boardId, email)) {
            throw new com.curatix.vault.exception.BadRequestException("User is already a member of this board");
        }

        Optional<BoardInvitationEntity> existingInvite = boardInvitationRepository.findByBoardIdAndRecipientEmailIgnoreCase(boardId, email);

        if (existingInvite.isPresent()) {
            BoardInvitationEntity invite = existingInvite.get();
            if (invite.getStatus() == BoardInvitationEntity.Status.PENDING) {
                throw new com.curatix.vault.exception.BadRequestException("An invitation is already pending for this email");
            }
            
            // Re-invite user who declined or was previously in the board
            invite.setStatus(BoardInvitationEntity.Status.PENDING);
            invite.setInviter(caller);
            invite.setRole(role);
            boardInvitationRepository.save(invite);
        } else {
            BoardEntity board = boardRepository.findById(boardId)
                    .orElseThrow(() -> new ResourceNotFoundException("Board not found"));

            BoardInvitationEntity invitation = BoardInvitationEntity.builder()
                    .board(board)
                    .inviter(caller)
                    .recipientEmail(email)
                    .role(role)
                    .status(BoardInvitationEntity.Status.PENDING)
                    .build();
            
            boardInvitationRepository.save(invitation);
        }
    }

    public List<BoardInvitationEntity> getMyInvitations(String firebaseUid) {
        UserEntity user = userService.getByFirebaseUid(firebaseUid);
        return boardInvitationRepository.findAllByRecipientEmailIgnoreCaseAndStatus(user.getEmail(), BoardInvitationEntity.Status.PENDING);
    }

    @Transactional
    public void respondToInvitation(String firebaseUid, Long inviteId, boolean accept) {
        UserEntity user = userService.getByFirebaseUid(firebaseUid);
        BoardInvitationEntity invite = boardInvitationRepository.findById(inviteId)
                .orElseThrow(() -> new ResourceNotFoundException("Invitation not found"));

        if (!invite.getRecipientEmail().equalsIgnoreCase(user.getEmail())) {
            throw new com.curatix.vault.exception.BadRequestException("This invitation is not for you");
        }

        if (invite.getStatus() != BoardInvitationEntity.Status.PENDING) {
            throw new com.curatix.vault.exception.BadRequestException("Invitation is already processed");
        }

        if (accept) {
            invite.setStatus(BoardInvitationEntity.Status.ACCEPTED);
            
            // Create membership
            BoardMemberEntity bm = BoardMemberEntity.builder()
                    .board(invite.getBoard())
                    .user(user)
                    .role(invite.getRole())
                    .build();
            boardMemberRepository.save(bm);

            // Sync profile
            memberProfileService.syncFromUser(invite.getBoard(), user);
        } else {
            invite.setStatus(BoardInvitationEntity.Status.DECLINED);
        }
        
        boardInvitationRepository.save(invite);
    }

    public Map<String, Object> getBoardStats(String firebaseUid, Long boardId) {
        getBoard(firebaseUid, boardId); // validates access
        long memberCount = boardMemberRepository.findAllByBoardId(boardId).size();
        return Map.of(
                "memberCount", memberCount,
                "boardId", boardId
        );
    }
}
