package com.topviec.topviec_be.entity;

import com.topviec.topviec_be.annotation.Trackable;
import com.topviec.topviec_be.enums.company.CompanyStatus;
import com.topviec.topviec_be.enums.company.VerificationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity lưu trữ thông tin hồ sơ công ty trên hệ thống TopViec.
 *
 * <p>
 * Vòng đời trạng thái:
 * 
 * <pre>
 *   [pending] → (admin duyệt) → [active]
 *   [pending] → (admin từ chối) → [pending] (NTT chỉnh sửa lại)
 *   [active]  → (vi phạm vượt ngưỡng) → [suspended]
 *   [suspended] → (admin mở khóa) → [active]
 *   [any]     → (xóa mềm) → [deleted]
 * </pre>
 *
 * <p>
 * Soft-delete thông qua {@code deleted_at}, mọi query tự động
 * filter bản ghi chưa xóa nhờ {@link SQLRestriction}.
 */
@Entity
@Table(name = "companies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private Long userId; // FK → users.id, chủ tài khoản công ty

    @Column(name = "slug", nullable = false, unique = true, length = 255)
    private String slug;

    @Trackable(label = "Tên công ty")
    @Column(name = "name", nullable = false, length = 300)
    private String name;

    @Trackable(label = "Logo")
    @Column(name = "logo_url", length = 512)
    private String logoUrl;

    @Column(name = "cover_url", length = 512)
    private String coverUrl;

    @Trackable(label = "Mô tả công ty")
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    /**
     * Lĩnh vực hoạt động chính.
     * FK → industries.id
     */
    @Trackable(label = "Lĩnh vực hoạt động")
    @Column(name = "industry_id", nullable = false)
    private Long industryId;

    /**
     * Quy mô công ty.
     * Giá trị hợp lệ: {@code 1-50}, {@code 51-200}, {@code 201-500}, {@code 500+}
     */
    @Trackable(label = "Quy mô công ty")
    @Column(name = "company_size", nullable = false, length = 20)
    private String companySize;

    @Trackable(label = "Năm thành lập")
    @Column(name = "founded_year", columnDefinition = "SMALLINT")
    private Integer foundedYear;

    @Trackable(label = "Website")
    @Column(name = "website", length = 512)
    private String website;

    @Trackable(label = "Email")
    @Column(name = "email", length = 255)
    private String email;

    @Trackable(label = "Số điện thoại")
    @Column(name = "phone", length = 20)
    private String phone;

    @Trackable(label = "Địa chỉ")
    @Column(name = "address", length = 512)
    private String address;

    /**
     * Thành phố .
     * FK → locations.id
     */
    @Column(name = "province_id")
    private Integer provinceId;

    @Column(name = "tax_code", unique = true, length = 20)
    private String taxCode;

    @Column(name = "business_license_url", length = 512)
    private String businessLicenseUrl;

    @Trackable(label = "Văn hóa công ty")
    @Column(name = "culture", columnDefinition = "TEXT")
    private String culture;

    /**
     * Phúc lợi dạng JSON linh hoạt.
     * Ví dụ: ["Bảo hiểm sức khỏe", "13 tháng lương", "Laptop"]
     */
    @Column(name = "benefits", columnDefinition = "JSON")
    private String benefits;

    @Column(name = "social_links", columnDefinition = "JSON")
    private String socialLinks;

    // -------------------------------------------------------------------------
    // Xét duyệt hồ sơ công ty
    // -------------------------------------------------------------------------

    /**
     * Trạng thái xét duyệt hồ sơ công ty bởi admin.
     * Giá trị hợp lệ: {@code pending} | {@code verified} | {@code rejected}.
     * Default: {@code pending}.
     */
    @Trackable(label = "Trạng thái xét duyệt")
    @Column(name = "verification_status", nullable = false, length = 20)
    @Builder.Default
    private String verificationStatus = VerificationStatus.PENDING.getValue();

    /** Thời điểm admin duyệt hồ sơ. */
    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    /**
     * Admin đã duyệt hồ sơ.
     * FK → users.id (admin)
     */
    @Column(name = "verified_by")
    private Long verifiedBy;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @Column(name = "violation_score", nullable = false)
    @Builder.Default
    private Integer violationScore = 0;

    /**
     * Trạng thái hoạt động của công ty.
     * Giá trị hợp lệ: {@code pending} | {@code active} | {@code suspended} |
     * {@code deleted}.
     * Default: {@code pending}.
     */
    @Trackable(label = "Trạng thái hoạt động")
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private String status = CompanyStatus.PENDING.getValue();

    @Column(name = "suspended_at")
    private LocalDateTime suspendedAt;

    /**
     * Lý do suspend.
     * Khác với {@link #rejectionReason} (dùng cho verification),
     * field này dùng cho trường hợp bị suspend sau khi đã active.
     */
    @Column(name = "suspended_reason", columnDefinition = "TEXT")
    private String suspendedReason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by", nullable = false, updatable = false)
    private Long createdBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by")
    private Long updatedBy;

    @Builder.Default
    @Column(name = "is_banner")
    private Boolean isBanner = false;

    @Builder.Default
    @Column(name = "is_top_employer")
    private Boolean isTopEmployer = false;

    @Builder.Default
    @Column(name = "is_brand_verified")
    private Boolean isBrandVerified = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @JsonIgnore
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Order> orders = new ArrayList<>();

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