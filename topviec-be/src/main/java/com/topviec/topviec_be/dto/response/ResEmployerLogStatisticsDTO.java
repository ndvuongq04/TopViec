package com.topviec.topviec_be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO thống kê log cho Employer Dashboard.
 *
 * 1. totalActivity      — thể hiện quy mô tương tác chung của công ty.
 * 2. candidateProcessing — tập trung vào các hành động thực tế trên hồ sơ ứng viên.
 * 3. dataUpdates         — giám sát các thay đổi về tin đăng và thông tin công ty.
 * 4. activeMembers       — các employer đang hoạt động trong công ty.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResEmployerLogStatisticsDTO {

    /** Thể hiện quy mô tương tác chung của công ty */
    private long totalActivity;

    /** Tập trung vào các hành động thực tế trên hồ sơ ứng viên */
    private long candidateProcessing;

    /** Giám sát các thay đổi về tin đăng và thông tin công ty */
    private long dataUpdates;

    /** Các employer đang hoạt động trong công ty */
    private long activeMembers;
}
