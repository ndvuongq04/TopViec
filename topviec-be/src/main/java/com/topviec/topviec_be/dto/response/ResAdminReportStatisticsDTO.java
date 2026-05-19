package com.topviec.topviec_be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResAdminReportStatisticsDTO {

    /** Tổng số báo cáo trong hệ thống (chưa bị xóa mềm) */
    private long totalReports;

    /** Số báo cáo đang chờ xử lý (status = pending) */
    private long pendingReports;

    /** Số báo cáo nhóm B — vi phạm nặng (violation_group = B, chưa xóa mềm) */
    private long groupBReports;

    /** Số báo cáo quá hạn SLA — chưa xử lý xong mà đã quá deadline */
    private long slaOverdueReports;
}
