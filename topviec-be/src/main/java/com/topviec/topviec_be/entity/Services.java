package com.topviec.topviec_be.entity;

import com.topviec.topviec_be.annotation.Trackable;
import com.topviec.topviec_be.enums.services.ServiceCategory;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "services")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Services {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", length = 100, unique = true, nullable = false)
    private String code;

    @Trackable(label = "Tên dịch vụ")
    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", length = 50)
    private ServiceCategory category;

    @Trackable(label = "Đơn vị")
    @Column(name = "unit", length = 50)
    private String unit;

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
