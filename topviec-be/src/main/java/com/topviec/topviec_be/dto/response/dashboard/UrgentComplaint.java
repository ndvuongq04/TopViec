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
public class UrgentComplaint {
    private Long complaintId;
    private String reportCode;
    private String priority;
    private String complaintType;
    private String companyName;
    private long waitingHours;
    private LocalDateTime createdAt;
}
