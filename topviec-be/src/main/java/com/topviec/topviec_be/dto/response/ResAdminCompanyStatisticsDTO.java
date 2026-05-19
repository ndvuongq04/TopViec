package com.topviec.topviec_be.dto.response;

import com.topviec.topviec_be.enums.services.BillingCycle;
import com.topviec.topviec_be.enums.services.SubscriptionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResAdminCompanyStatisticsDTO {

    /** Tổng số tin tuyển dụng đã đăng (chưa bị xóa mềm) */
    private long totalJobPostings;

    /** Tổng số CV/đơn ứng tuyển đã nhận (chưa bị xóa mềm) */
    private long totalApplicationsReceived;

    /** Danh sách gói dịch vụ đang sử dụng (status = ACTIVE) */
    private List<ActiveSubscriptionDTO> activeSubscriptions;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActiveSubscriptionDTO {
        private Long subscriptionId;
        private Long servicePackageId;
        private String packageName;
        private String packageCode;
        private BillingCycle billingCycle;
        private SubscriptionStatus status;
        private LocalDateTime startedAt;
        private LocalDateTime expiredAt;
    }
}
