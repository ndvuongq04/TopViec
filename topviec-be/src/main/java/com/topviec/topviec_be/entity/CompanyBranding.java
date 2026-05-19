package com.topviec.topviec_be.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.topviec.topviec_be.enums.services.BrandingAddonStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "company_brandings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyBranding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", insertable = false, updatable = false)
    private Company company;

    @Column(name = "company_addon_id", nullable = false)
    private Long companyAddonId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_addon_id", insertable = false, updatable = false)
    private CompanyAddon companyAddon;

    @Column(name = "addon_service_id", nullable = false)
    private Long addonServiceId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "addon_service_id", insertable = false, updatable = false)
    private AddonService addonService;

    @Column(name = "service_code", length = 100, nullable = false)
    private String serviceCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private BrandingAddonStatus status;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = BrandingAddonStatus.ACTIVE;
        }
    }
}
