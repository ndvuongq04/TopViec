package com.topviec.topviec_be.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ReminderInfo {
    private Integer reminderCount;
    private LocalDateTime lastRemindedAt;
    private LocalDateTime deadline;
}