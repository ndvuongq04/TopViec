package com.topviec.topviec_be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Response điểm vi phạm dành cho NTD xem thông tin của chính mình.
 * Không bao gồm lịch sử chi tiết từng lần vi phạm (dành riêng cho Admin).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResMyViolationScoreDTO {

    private Integer totalScore;

    /**
     * Mức độ vi phạm hiện tại.
     * Giá trị: {@code normal} (0–19) | {@code limited} (20–49) | {@code suspended} (≥50)
     */
    private String scoreLevel;

    /**
     * Mô tả hạn chế áp dụng tương ứng với scoreLevel.
     * VD: "Chỉ được đăng tối đa 3 tin/tuần, tin mới cần Admin duyệt trước khi hiển thị"
     */
    private String restrictionDescription;

    /**
     * Thời điểm vi phạm nhóm B gần nhất.
     * Dùng để NTD biết khi nào đủ điều kiện yêu cầu Admin reset điểm (sau 6 tháng).
     */
    private LocalDateTime lastGroupBViolationAt;

    /**
     * NTD đã đủ điều kiện để liên hệ Admin yêu cầu reset điểm về 0 hay chưa.
     * true nếu chưa từng vi phạm nhóm B hoặc đã qua 6 tháng kể từ lần vi phạm nhóm B gần nhất.
     */
    private Boolean canRequestReset;

    private String companyStatus;
}
