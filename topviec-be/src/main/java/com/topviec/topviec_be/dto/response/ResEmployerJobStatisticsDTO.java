package com.topviec.topviec_be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResEmployerJobStatisticsDTO {

    /** Tổng số tin tuyển dụng (chưa xóa mềm) của công ty */
    private long totalJobPosts;

    /** Tổng số tin đang hoạt động (trạng thái 'published' hoặc 'interviewing') */
    private long activeJobPosts;

    /** Tổng số tin đang chờ duyệt (trạng thái 'pending_approval') */
    private long pendingJobPosts;

    /** Tổng số tin sắp hết hạn (còn <= 7 ngày, đang trong trạng thái hoạt động) */
    private long expiringJobPosts;
}
