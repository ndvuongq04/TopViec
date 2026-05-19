package com.topviec.topviec_be.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "service_package_details", uniqueConstraints = @UniqueConstraint(columnNames = { "service_package_id",
        "service_id" }))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServicePackageDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "service_package_id", nullable = false)
    private Long servicePackageId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_package_id", insertable = false, updatable = false)
    private ServicePackage servicePackage;

    @Column(name = "service_id", nullable = false)
    private Long serviceId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", insertable = false, updatable = false)
    private Services service;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;
}
