package com.topviec.topviec_be.service.impl;

import com.topviec.topviec_be.dto.internal.LogContext;
import com.topviec.topviec_be.entity.AuditLog;
import com.topviec.topviec_be.entity.BusinessEventLog;
import com.topviec.topviec_be.enums.logging.LogActionType;
import com.topviec.topviec_be.enums.logging.LogType;
import com.topviec.topviec_be.repository.AuditLogRepository;
import com.topviec.topviec_be.repository.BusinessEventLogRepository;
import com.topviec.topviec_be.service.LoggingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoggingServiceImpl implements LoggingService {

    private final AuditLogRepository auditLogRepository;
    private final BusinessEventLogRepository businessEventLogRepository;

    @Override
    @Async("loggingTaskExecutor")
    public void saveLogAsync(LogContext context) {
        try {
            LogActionType actionType = context.getActionType();
            LogType logType = actionType.getLogType();

            if (logType == LogType.AUDIT || logType == LogType.BOTH) {
                saveAuditLog(context, actionType);
            }

            if (logType == LogType.BUSINESS || logType == LogType.BOTH) {
                saveBusinessEventLog(context, actionType);
            }

        } catch (Exception e) {
            // Log lỗi nhưng KHÔNG throw — tránh ảnh hưởng business logic
            log.error("[Logging] Lỗi khi lưu log action={}: {}",
                    context.getActionType(), e.getMessage(), e);
        }
    }

    private void saveAuditLog(LogContext context, LogActionType actionType) {
        AuditLog auditLog = AuditLog.builder()
                .userId(context.getUserId())
                .action(actionType.name())
                .category(actionType.getCategory().name())
                .severity(actionType.getSeverity().name())
                .targetEntity(actionType.getTargetEntity())
                .targetId(context.getTargetId())
                .description(context.getDescription())
                .ipAddress(context.getIpAddress())
                .userAgent(context.getUserAgent())
                .status(context.getStatus())
                .durationMs(context.getDurationMs())
                .errorMessage(context.getErrorMessage())
                .build();

        auditLogRepository.save(auditLog);
        log.debug("[AuditLog] Saved: action={}, userId={}, targetId={}",
                actionType.name(), context.getUserId(), context.getTargetId());
    }

    private void saveBusinessEventLog(LogContext context, LogActionType actionType) {
        BusinessEventLog eventLog = BusinessEventLog.builder()
                .userId(context.getUserId())
                .action(actionType.name())
                .category(actionType.getCategory().name())
                .targetEntity(actionType.getTargetEntity())
                .targetId(context.getTargetId())
                .metadata(context.getMetadata())
                .status(context.getStatus())
                .durationMs(context.getDurationMs())
                .build();

        businessEventLogRepository.save(eventLog);
        log.debug("[BusinessEventLog] Saved: action={}, userId={}, targetId={}",
                actionType.name(), context.getUserId(), context.getTargetId());
    }
}
