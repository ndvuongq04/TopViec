package com.topviec.topviec_be.dto.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResManagerDashboardDTO {
    private long activeJobs;
    private long pendingApplications;
    private long upcomingInterviews;
    private Map<String, Long> applicationsByStatus;
    private List<PendingCandidateDTO> pendingCandidates;
}
