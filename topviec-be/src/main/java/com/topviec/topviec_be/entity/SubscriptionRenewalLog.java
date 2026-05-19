package com.topviec.topviec_be.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "subscription_renewal_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionRenewalLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_subscription_id", nullable = false)
    private Long companySubscriptionId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_subscription_id", insertable = false, updatable = false)
    private CompanySubscription companySubscription;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", insertable = false, updatable = false)
    private Order order;

    @Column(name = "old_expired_at", nullable = false)
    private LocalDateTime oldExpiredAt;

    @Column(name = "new_expired_at", nullable = false)
    private LocalDateTime newExpiredAt;

    /** Số quota được cộng thêm cho mỗi feature */
    @Column(name = "quota_added", nullable = false)
    private Integer quotaAdded;

    @Column(name = "renewed_by", nullable = false)
    private Long renewedBy;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
