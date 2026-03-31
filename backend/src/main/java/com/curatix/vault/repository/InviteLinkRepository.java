package com.curatix.vault.repository;

import com.curatix.vault.entity.InviteLinkEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InviteLinkRepository extends JpaRepository<InviteLinkEntity, Long> {
    Optional<InviteLinkEntity> findByToken(String token);
    java.util.List<InviteLinkEntity> findAllByBoardIdAndActiveTrue(Long boardId);
}
