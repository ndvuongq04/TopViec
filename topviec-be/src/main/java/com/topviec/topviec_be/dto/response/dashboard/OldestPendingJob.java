package com.topviec.topviec_be.dto.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OldestPendingJob {
    private Long jobId;
    private String title;
    private String companyName;
    private LocalDateTime createdAt;
    private long waitingDays;
}
