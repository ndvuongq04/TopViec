package com.topviec.topviec_be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.topviec.topviec_be.entity.User;
import com.topviec.topviec_be.enums.users.UserStatus;
import com.topviec.topviec_be.enums.users.UserType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.lastLoginAt = :lastLoginAt, u.lastLoginIp = :lastLoginIp WHERE u.id = :userId")
    void updateLastLogin(@Param("userId") Long userId,
            @Param("lastLoginAt") LocalDateTime lastLoginAt,
            @Param("lastLoginIp") String lastLoginIp);

    /** Lấy danh sách userId theo userType — dùng cho log role filter */
    @Query("SELECT u.id FROM User u WHERE u.userType = :userType AND u.deletedAt IS NULL")
    List<Long> findAllByUserType(@Param("userType") UserType userType);

    long countByUserTypeAndStatusAndDeletedAtIsNull(UserType userType, UserStatus status);

    @Query("""
            SELECT FUNCTION('DATE', u.createdAt), COUNT(u)
            FROM User u
            WHERE u.userType = :userType
            AND u.createdAt >= :startDate
            AND u.createdAt < :endDate
            AND u.deletedAt IS NULL
            GROUP BY FUNCTION('DATE', u.createdAt)
            ORDER BY FUNCTION('DATE', u.createdAt)
            """)
    List<Object[]> countNewUsersByTypeAndDateRange(
            @Param("userType") UserType userType,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
