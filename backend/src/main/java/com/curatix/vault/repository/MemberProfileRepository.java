package com.curatix.vault.repository;

import com.curatix.vault.entity.MemberProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberProfileRepository extends JpaRepository<MemberProfileEntity, Long> {
    List<MemberProfileEntity> findAllByBoardId(Long boardId);
    boolean existsByIdAndBoardId(Long id, Long boardId);
    java.util.Optional<MemberProfileEntity> findByBoardIdAndUserId(Long boardId, Long userId);
    List<MemberProfileEntity> findAllByUserId(Long userId);
    List<MemberProfileEntity> findAllByBoardIdAndUserIdIn(Long boardId, java.util.Collection<Long> userIds);
}
