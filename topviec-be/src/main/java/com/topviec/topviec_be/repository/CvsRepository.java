package com.topviec.topviec_be.repository;

import com.topviec.topviec_be.entity.Cvs;
import com.topviec.topviec_be.enums.cvs.CvType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CvsRepository extends JpaRepository<Cvs, Long> {

    // Đếm số CV của user — dùng kiểm tra giới hạn 5 CV
    @Query("SELECT COUNT(c) FROM Cvs c WHERE c.userId = :userId AND c.deletedAt IS NULL")
    long countByUserId(@Param("userId") Long userId);

    // Kiểm tra tên CV đã tồn tại chưa — dùng validate trước khi tạo/đổi tên
    @Query("SELECT COUNT(c) > 0 FROM Cvs c WHERE c.userId = :userId AND c.title = :title AND c.deletedAt IS NULL")
    boolean existsByUserIdAndTitle(@Param("userId") Long userId, @Param("title") String title);

    // Lấy danh sách CV của user — dùng cho màn hình "CV của tôi"
    @Query("SELECT c FROM Cvs c WHERE c.userId = :userId AND c.deletedAt IS NULL ORDER BY c.createdAt DESC")
    List<Cvs> findAllByUserId(@Param("userId") Long userId);

    // Lấy CV mặc định — dùng khi set CV mới làm mặc định (tắt cái cũ trước)
    @Query("SELECT c FROM Cvs c WHERE c.userId = :userId AND c.isDefault = true AND c.deletedAt IS NULL")
    Optional<Cvs> findDefaultByUserId(@Param("userId") Long userId);

    // Lấy CV theo id và userId — tránh user A xem/sửa CV của user B
    @Query("SELECT c FROM Cvs c WHERE c.id = :id AND c.userId = :userId AND c.deletedAt IS NULL")
    Optional<Cvs> findByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    // Lấy CV qua share_token — dùng cho tính năng xem CV công khai
    @Query("SELECT c FROM Cvs c WHERE c.shareToken = :shareToken AND c.deletedAt IS NULL")
    Optional<Cvs> findByShareToken(@Param("shareToken") String shareToken);

    @Query("SELECT c FROM Cvs c WHERE c.id = :id AND c.deletedAt IS NULL")
    Optional<Cvs> findActiveById(@Param("id") Long id);

    @Query("SELECT COUNT(c) FROM Cvs c WHERE c.fileUrl = :fileUrl AND c.deletedAt IS NULL")
    long countActiveByFileUrl(@Param("fileUrl") String fileUrl);

    @Query("SELECT COUNT(c) FROM Cvs c WHERE c.pdfUrl = :pdfUrl AND c.deletedAt IS NULL")
    long countActiveByPdfUrl(@Param("pdfUrl") String pdfUrl);

    @Modifying
    @Query("""
            UPDATE Cvs c
            SET c.pdfDirty = true
            WHERE c.templateId = :templateId
            AND c.cvType = :cvType
            AND c.deletedAt IS NULL
            """)
    int markPdfDirtyByTemplateId(
            @Param("templateId") Long templateId,
            @Param("cvType") CvType cvType);
}
