package com.topviec.topviec_be.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "job_renewal_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobRenewalLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "job_post_id", nullable = false)
    private Long jobPostId;

    @Column(name = "renewed_by", nullable = false)
    private Long renewedBy;

    // Chỉ chấp nhận 3 giá trị: 7, 15, 30
    @Column(name = "days_added", nullable = false)
    private Integer daysAdded;

    @Column(name = "old_deadline", nullable = false)
    private LocalDateTime oldDeadline;

    // Phải bằng old_deadline + days_added
    @Column(name = "new_deadline", nullable = false)
    private LocalDateTime newDeadline;

    // NULL = gia hạn miễn phí theo gói subscription
    // Có giá trị = mua gia hạn riêng lẻ, tính phí
    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Relationships (comment để mở sau khi có đủ các entity liên quan)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_post_id", insertable = false, updatable = false)
    private JobPosting jobPosting;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}