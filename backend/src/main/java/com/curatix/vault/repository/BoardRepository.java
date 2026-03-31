package com.curatix.vault.repository;

import com.curatix.vault.entity.BoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<BoardEntity, Long> {

    // All boards where user is a member (any role), not deleted
    @Query("""
        SELECT b FROM BoardEntity b
        JOIN BoardMemberEntity bm ON bm.board = b
        WHERE bm.user.id = :userId AND b.deleted = false
        ORDER BY b.updatedAt DESC
    """)
    List<BoardEntity> findAllByMemberUserId(@Param("userId") Long userId);

    // Active (non-deleted) board by id
    Optional<BoardEntity> findByIdAndDeletedFalse(Long id);
}
