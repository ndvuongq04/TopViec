package com.topviec.topviec_be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import com.topviec.topviec_be.entity.UserSession;
import java.util.List;

public interface UserSessionRepository extends JpaRepository<UserSession, Long> {
    Optional<UserSession> findByRefreshTokenHash(String refreshTokenHash);

    List<UserSession> findAllByUserId(Long userId);
}