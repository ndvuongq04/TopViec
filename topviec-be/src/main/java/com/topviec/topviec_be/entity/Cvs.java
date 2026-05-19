package com.topviec.topviec_be.entity;

import com.topviec.topviec_be.enums.cvs.CvParseStatus;
import com.topviec.topviec_be.enums.cvs.CvType;
import com.topviec.topviec_be.enums.cvs.CvVisibility;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "cvs", uniqueConstraints = {
        // Mỗi user không được có 2 CV trùng tên
        @UniqueConstraint(name = "uk_cvs_user_title", columnNames = { "user_id", "title" })
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cvs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "title", nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "cv_type", nullable = false)
    private CvType cvType;

    @Column(name = "template_id")
    private Long templateId;

    @Column(name = "file_url")
    private String fileUrl;

    @Column(name = "pdf_url")
    private String pdfUrl;

    @Column(name = "pdf_dirty", nullable = false, columnDefinition = "boolean default false")
    @Builder.Default
    private Boolean pdfDirty = false;

    @Column(name = "cv_section_hash", length = 64)
    private String cvSectionHash;

    @Column(name = "is_default", nullable = false)
    @Builder.Default
    private Boolean isDefault = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false)
    @Builder.Default
    private CvVisibility visibility = CvVisibility.private_cv;

    @Column(name = "share_token", unique = true)
    private String shareToken;

    @Column(name = "share_expires_at")
    private LocalDateTime shareExpiresAt;

    @Column(name = "extra_data", columnDefinition = "JSON")
    private String extraData;

    @Enumerated(EnumType.STRING)
    @Column(name = "parse_status")
    @Builder.Default
    private CvParseStatus parseStatus = CvParseStatus.skipped;

    @Column(name = "view_count", nullable = false)
    @Builder.Default
    private Integer viewCount = 0;

    // @Column(name = "level_id", nullable = false)
    // private Long levelId;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by", updatable = false, nullable = false)
    private Long createdBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
