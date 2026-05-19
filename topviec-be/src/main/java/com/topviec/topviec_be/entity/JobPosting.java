package com.topviec.topviec_be.entity;

import com.topviec.topviec_be.annotation.Trackable;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "job_postings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPosting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @Column(name = "created_by_user_id", nullable = false)
    private Long createdByUserId;

    @Trackable(label = "Tiêu đề")
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "slug", nullable = false, unique = true)
    private String slug;

    @Trackable(label = "Mô tả công việc")
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Trackable(label = "Yêu cầu")
    @Column(name = "requirements", nullable = false, columnDefinition = "TEXT")
    private String requirements;

    @Trackable(label = "Quyền lợi")
    @Column(name = "benefits", columnDefinition = "TEXT")
    private String benefits;

    @Trackable(label = "Ngành nghề")
    @Column(name = "industry_id", nullable = false)
    private Long industryId;

    @Trackable(label = "Cấp bậc")
    @Column(name = "level_id", nullable = false)
    private Long levelId;

    @Trackable(label = "Kinh nghiệm tối thiểu (năm)")
    @Column(name = "experience_years_min", nullable = false)
    private Integer experienceYearsMin;

    @Trackable(label = "Kinh nghiệm tối đa (năm)")
    @Column(name = "experience_years_max")
    private Integer experienceYearsMax;

    @Trackable(label = "Lương tối thiểu")
    @Column(name = "salary_min")
    private Long salaryMin;

    @Trackable(label = "Lương tối đa")
    @Column(name = "salary_max")
    private Long salaryMax;

    @Trackable(label = "Lương thỏa thuận")
    @Column(name = "salary_negotiable", nullable = false)
    private Boolean salaryNegotiable;

    // Dùng String — nhất quán với pattern toàn project
    // Giá trị hợp lệ: full_time | part_time | remote | hybrid | freelance (WorkType
    // enum)
    @Trackable(label = "Hình thức làm việc")
    @Column(name = "work_type", nullable = false, length = 20)
    private String workType;

    @Trackable(label = "Số lượng tuyển")
    @Column(name = "headcount", nullable = false)
    private Integer headcount;

    @Trackable(label = "Hạn nộp hồ sơ")
    @Column(name = "deadline", nullable = false)
    private LocalDateTime deadline;

    // Giá trị hợp lệ: xem JobPostStatus enum
    @Trackable(label = "Trạng thái")
    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    // Giá trị hợp lệ: filled | cancelled | other (CloseReason enum)
    @Column(name = "close_reason")
    private String closeReason;

    @Column(name = "close_note", columnDefinition = "TEXT")
    private String closeNote;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;

    @Column(name = "is_featured", nullable = false)
    private Boolean isFeatured;

    @Column(name = "is_urgent", nullable = false)
    private Boolean isUrgent;

    @Column(name = "view_count", nullable = false)
    private Integer viewCount;

    @Column(name = "edit_count", nullable = false)
    private Integer editCount;

    @Column(name = "refreshed_at")
    private LocalDateTime refreshedAt;

    @Builder.Default
    @Column(name = "is_hot")
    private Boolean isHot = false;

    @Column(name = "moderation_note", columnDefinition = "TEXT")
    private String moderationNote;

    // Giá trị hợp lệ: spam | fraudulent | wrong_info | incomplete | other
    // (RejectionReason enum)
    @Column(name = "rejection_reason")
    private String rejectionReason;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // Vector embedding cho hệ thống gợi ý việc làm (pgvector - 1536 chiều)
    // Yêu cầu extension pgvector trong PostgreSQL và dependency hibernate-vector
    // @Column(name = "embedding", columnDefinition = "vector(1536)")
    // @JdbcTypeCode(SqlTypes.VECTOR)
    // private float[] embedding;

    @Builder.Default
    @OneToMany(mappedBy = "jobPosting", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<JobPostSkill> skills = new ArrayList<>();

    @OneToMany(mappedBy = "jobPosting", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<JobPostLocation> locations = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.viewCount == null)
            this.viewCount = 0;
        if (this.editCount == null)
            this.editCount = 0;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}