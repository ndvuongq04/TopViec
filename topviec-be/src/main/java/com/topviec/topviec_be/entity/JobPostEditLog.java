package com.topviec.topviec_be.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDateTime;

@Entity
@Table(name = "job_post_edit_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPostEditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "job_post_id", nullable = false)
    private Long jobPostId;

    @Column(name = "edited_by", nullable = false)
    private Long editedBy;

    // Snapshot toàn bộ dữ liệu của tin trước khi chỉnh sửa (dùng cho rollback /
    // audit)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "snapshot_before", nullable = false, columnDefinition = "json")
    private String snapshotBefore;

    @Column(name = "edit_type", nullable = false)
    private String editType;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}