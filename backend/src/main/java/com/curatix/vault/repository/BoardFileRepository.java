package com.curatix.vault.repository;

import com.curatix.vault.entity.BoardFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardFileRepository extends JpaRepository<BoardFileEntity, Long> {
    List<BoardFileEntity> findAllByBoardIdOrderByUploadedAtDesc(Long boardId);
    boolean existsByIdAndBoardId(Long id, Long boardId);
}
