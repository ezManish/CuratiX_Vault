package com.curatix.vault.repository;

import com.curatix.vault.entity.BoardInvitationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface BoardInvitationRepository extends JpaRepository<BoardInvitationEntity, Long> {
    @Query("SELECT i FROM BoardInvitationEntity i JOIN FETCH i.board JOIN FETCH i.inviter " +
           "WHERE LOWER(i.recipientEmail) = LOWER(:email) AND i.status = :status")
    List<BoardInvitationEntity> findAllByRecipientEmailIgnoreCaseAndStatus(
            @Param("email") String email, 
            @Param("status") BoardInvitationEntity.Status status);

    Optional<BoardInvitationEntity> findByBoardIdAndRecipientEmailIgnoreCaseAndStatus(Long boardId, String email, BoardInvitationEntity.Status status);
    Optional<BoardInvitationEntity> findByBoardIdAndRecipientEmailIgnoreCase(Long boardId, String email);
}
