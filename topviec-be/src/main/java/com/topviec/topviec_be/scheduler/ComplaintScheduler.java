package com.topviec.topviec_be.scheduler;

import com.topviec.topviec_be.service.ReportService;
import com.topviec.topviec_be.service.ViolationScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ComplaintScheduler {

    private final ReportService reportService;
    private final ViolationScoreService violationScoreService;

    /**
     * Chạy mỗi giờ: ẩn tin và đóng báo cáo nhóm A khi NTD không sửa trong 48h.
     */
    @Scheduled(fixedRate = 60 * 60 * 1_000)
    public void autoCloseExpiredGroupAComplaints() {
        reportService.autoCloseExpiredGroupAComplaints();
    }

    /**
     * Chạy vào 02:00 ngày đầu tiên mỗi tháng:
     * reset điểm cho NTD đủ điều kiện không tái phạm nhóm B.
     */
    @Scheduled(cron = "0 0 2 1 * *")
    public void autoResetEligibleViolationScores() {
        violationScoreService.autoResetEligibleScores();
    }
}
