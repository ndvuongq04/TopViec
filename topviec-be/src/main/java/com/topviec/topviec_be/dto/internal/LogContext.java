package com.topviec.topviec_be.dto.internal;

import com.topviec.topviec_be.enums.logging.LogActionType;
import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * Internal DTO — truyền dữ liệu log qua AOP → LoggingService.
 * Không phải entity, không persist trực tiếp.
 */
@Data
@Builder
public class LogContext {

    private LogActionType actionType;
    private Long userId;
    private String ipAddress;
    private String userAgent;
    private Long targetId;
    private String description;

    /** Metadata bổ sung cho Business Event Log (oldStatus, newStatus, jobTitle, ...) */
    @Builder.Default
    private Map<String, Object> metadata = new HashMap<>();

    private long startTime;

    /** SUCCESS | FAILURE */
    private String status;
    private long durationMs;
    private String errorMessage;

    // ── Convenience methods cho service layer gọi qua LogContextHolder ──

    public void addMetadata(String key, Object value) {
        if (this.metadata == null) {
            this.metadata = new HashMap<>();
        }
        this.metadata.put(key, value);
    }
}
