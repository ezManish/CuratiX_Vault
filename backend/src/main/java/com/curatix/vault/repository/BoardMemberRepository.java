package com.curatix.vault.repository;

import com.curatix.vault.entity.BoardMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BoardMemberRepository extends JpaRepository<BoardMemberEntity, Long> {
    Optional<BoardMemberEntity> findByBoardIdAndUserId(Long boardId, Long userId);
    boolean existsByBoardIdAndUserId(Long boardId, Long userId);
    
    @Query("SELECT (COUNT(bm) > 0) FROM BoardMemberEntity bm WHERE bm.board.id = :boardId AND LOWER(bm.user.email) = LOWER(:email)")
    boolean existsByBoardIdAndUserEmail(@Param("boardId") Long boardId, @Param("email") String email);
    
    @Query("SELECT bm FROM BoardMemberEntity bm JOIN FETCH bm.user WHERE bm.board.id = :boardId")
    java.util.List<BoardMemberEntity> findAllByBoardIdJoinUser(@Param("boardId") Long boardId);

    java.util.List<BoardMemberEntity> findAllByBoardId(Long boardId);
}
