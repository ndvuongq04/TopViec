package com.topviec.topviec_be.service;

import com.topviec.topviec_be.dto.internal.LogContext;

public interface LoggingService {

    /**
     * Lưu log bất đồng bộ — tự routing dựa trên LogType trong LogContext.
     * AUDIT → audit_logs, BUSINESS → business_event_logs, BOTH → cả 2.
     */
    void saveLogAsync(LogContext context);
}
