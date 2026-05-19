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
public class ResSupportAdminDashboardDTO {
    private long pendingComplaints;
    private long pendingAppeals;
    private long restrictedEmployers;
    private Map<String, Long> complaintsByStatus;
    private List<UrgentComplaint> urgentComplaints;
}
