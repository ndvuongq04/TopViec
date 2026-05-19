package com.topviec.topviec_be.entity;

import com.topviec.topviec_be.annotation.Trackable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "addon_services")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddonService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "service_id", nullable = false)
    private Long serviceId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", insertable = false, updatable = false)
    private Services service;

    @Trackable(label = "Tên addon")
    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @Column(name = "code", length = 100, unique = true, nullable = false)
    private String code;

    @Trackable(label = "Số lượng")
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Trackable(label = "Thời hạn (ngày)")
    @Column(name = "duration_days")
    private Integer durationDays;

    @Trackable(label = "Giá")
    @Column(name = "price", precision = 15, scale = 2)
    private BigDecimal price;

    @Trackable(label = "Mô tả")
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Trackable(label = "Trạng thái kích hoạt")
    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.isActive == null) {
            this.isActive = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
