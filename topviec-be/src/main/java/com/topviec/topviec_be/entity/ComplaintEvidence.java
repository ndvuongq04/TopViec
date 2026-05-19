package com.topviec.topviec_be.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Bằng chứng đính kèm cho khiếu nại nhóm B (fraudulent, payment_issue).
 * Bắt buộc có ít nhất 1 bằng chứng — validate tại tầng service khi tạo complaint.
 */
@Entity
@Table(name = "complaint_evidences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComplaintEvidence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /** FK → complaints.id */
    @Column(name = "complaint_id", nullable = false)
    private Long complaintId;

    @Column(name = "file_url", nullable = false, length = 512)
    private String fileUrl;

    /**
     * Loại file bằng chứng.
     * Giá trị hợp lệ: {@code image} | {@code pdf} | {@code video}
     */
    @Column(name = "file_type", nullable = false, length = 10)
    private String fileType;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
