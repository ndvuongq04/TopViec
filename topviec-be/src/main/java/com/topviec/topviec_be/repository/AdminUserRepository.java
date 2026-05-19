package com.topviec.topviec_be.repository;

import com.topviec.topviec_be.entity.AdminUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {

    @Query("SELECT a FROM AdminUser a WHERE a.adminUsersId = :id ")
    Optional<AdminUser> findActiveById(@Param("id") Long id);

    @Query("SELECT a FROM AdminUser a WHERE a.user.id = :userId AND a.isActive = true AND a.deletedAt IS NULL")
    Optional<AdminUser> findActiveByUserId(@Param("userId") Long userId);

    boolean existsByUser_Id(Long userId);

    // Trả về Page để hỗ trợ phân trang
    @Query("SELECT a FROM AdminUser a WHERE a.deletedAt IS NULL")
    Page<AdminUser> findAllNotDeleted(Pageable pageable);

    @Query("SELECT a FROM AdminUser a WHERE a.adminRole = :role AND a.deletedAt IS NULL")
    List<AdminUser> findAllByRole(@Param("role") String role);

    @Query("SELECT a FROM AdminUser a WHERE a.department = :department AND a.deletedAt IS NULL")
    List<AdminUser> findAllByDepartment(@Param("department") String department);

    /**
     * Tìm kiếm + lọc theo role — cả 2 optional:
     * - keyword null → không lọc theo tên
     * - adminRole null → không lọc theo role
     */
    @Query("""
            SELECT a FROM AdminUser a
            WHERE a.deletedAt IS NULL
            AND (:keyword IS NULL OR LOWER(a.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')))
            AND (:adminRole IS NULL OR a.adminRole = :adminRole)
            """)
    Page<AdminUser> findAllWithFilter(
            @Param("keyword") String keyword,
            @Param("adminRole") String adminRole,
            Pageable pageable);

    /** Batch load admin users theo danh sách userId — dùng cho log role resolution */
    @Query("SELECT a FROM AdminUser a WHERE a.user.id IN :userIds AND a.deletedAt IS NULL")
    List<AdminUser> findByUserIdIn(@Param("userIds") List<Long> userIds);

    /** Lấy tất cả userId của admin đang active — dùng cho log statistics */
    @Query("SELECT a.user.id FROM AdminUser a WHERE a.isActive = true AND a.deletedAt IS NULL")
    List<Long> findAllActiveAdminUserIds();
}