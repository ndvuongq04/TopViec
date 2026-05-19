package com.topviec.topviec_be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResAdminCandidateStatisticsDTO {
    private long totalCvs;
    private long totalApplications;
    private long totalFollowedCompanies;
    private long totalSavedJobs;
}
