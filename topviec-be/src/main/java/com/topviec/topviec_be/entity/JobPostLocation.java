package com.topviec.topviec_be.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "job_post_locations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPostLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "job_post_id", nullable = false)
    private Long jobPostId;

    @Column(name = "province_id", nullable = false)
    private Long provinceId;

    // Địa chỉ cụ thể trong tỉnh/thành phố. NULL nếu chỉ cần xác định đến cấp tỉnh
    @Column(name = "address_detail", columnDefinition = "TEXT")
    private String addressDetail;

    @Column(name = "is_remote", nullable = false)
    private Boolean isRemote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_post_id", insertable = false, updatable = false)
    private JobPosting jobPosting;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "province_id", insertable = false, updatable = false)
    private Location province;
}