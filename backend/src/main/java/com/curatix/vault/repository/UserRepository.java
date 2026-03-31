package com.curatix.vault.repository;

import com.curatix.vault.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByFirebaseUid(String firebaseUid);
    Optional<UserEntity> findByEmail(String email);
    boolean existsByFirebaseUid(String firebaseUid);
}
