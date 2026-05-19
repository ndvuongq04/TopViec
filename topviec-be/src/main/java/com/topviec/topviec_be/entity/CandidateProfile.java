package com.topviec.topviec_be.entity;

import com.topviec.topviec_be.annotation.Trackable;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "candidate_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Trackable(label = "Họ và tên")
    @Column(name = "full_name", nullable = false, length = 255)
    private String fullName;

    @Column(name = "avatar_url", length = 512)
    private String avatarUrl;

    @Trackable(label = "Ngày sinh")
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Trackable(label = "Giới tính")
    @Column(name = "gender", length = 10)
    private String gender;

    @Trackable(label = "Số điện thoại")
    @Column(name = "phone_display", length = 20)
    private String phoneDisplay;

    @Trackable(label = "Giới thiệu bản thân")
    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Column(name = "linkedin_url", length = 512)
    private String linkedinUrl;

    @Column(name = "github_url", length = 512)
    private String githubUrl;

    @Column(name = "personal_website", length = 512)
    private String personalWebsite;

    @Trackable(label = "Lương mong muốn tối thiểu")
    @Column(name = "expected_salary_min")
    private Double expectedSalaryMin;

    @Trackable(label = "Lương mong muốn tối đa")
    @Column(name = "expected_salary_max")
    private Double expectedSalaryMax;

    @Column(name = "salary_negotiable", nullable = false)
    @Builder.Default
    private Boolean salaryNegotiable = false;

    @Trackable(label = "Trạng thái tìm việc")
    @Column(name = "job_seeking_status", nullable = false, length = 20)
    @Builder.Default
    private String jobSeekingStatus = "active";

    @Trackable(label = "Vị trí mong muốn")
    @Column(name = "preferred_job_title", length = 255)
    private String preferredJobTitle;

    @Trackable(label = "Hình thức làm việc mong muốn")
    @Column(name = "preferred_work_type", length = 100)
    private String preferredWorkType;

    @Column(name = "preferred_location_id")
    private Integer preferredLocationId;

    @Column(name = "profile_completion_pct")
    @Builder.Default
    private Integer profileCompletionPct = 0;

    @Column(name = "is_cv_public", nullable = false)
    @Builder.Default
    private Boolean cvPublic = true;

    @Column(name = "hide_phone", nullable = false)
    @Builder.Default
    private Boolean hidePhone = false;

    @Column(name = "hide_email", nullable = false)
    @Builder.Default
    private Boolean hideEmail = false;

    @Column(name = "hide_date_of_birth", nullable = false)
    @Builder.Default
    private Boolean hideDateOfBirth = false;

    @Column(name = "hide_expected_salary", nullable = false)
    @Builder.Default
    private Boolean hideExpectedSalary = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by", nullable = false, updatable = false)
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
