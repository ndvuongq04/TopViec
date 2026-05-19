package com.topviec.topviec_be.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "cv_certifications", indexes = {
        @Index(name = "idx_cv_certifications_cv_id", columnList = "cv_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CvCertification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cv_id", nullable = false)
    private Long cvId;

    @Column(name = "name")
    private String name;

    @Column(name = "issuer")
    private String issuer;

    @Column(name = "issued_date")
    private LocalDateTime issuedDate;

    @Column(name = "expired_date")
    private LocalDateTime expiredDate;

    @Column(name = "credential_url")
    private String credentialUrl;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;
}
