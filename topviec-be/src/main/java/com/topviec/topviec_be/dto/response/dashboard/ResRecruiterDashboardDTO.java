package com.topviec.topviec_be.dto.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResRecruiterDashboardDTO {
    private long assignedActiveJobs;
    private long pendingApplications;
    private long upcomingInterviews;
    private List<JobApplicationCount> applicationsByJob;
    private List<PendingCandidateDTO> pendingCandidates;
}
