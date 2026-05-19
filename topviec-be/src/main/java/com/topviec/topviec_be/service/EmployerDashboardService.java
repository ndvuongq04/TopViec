package com.topviec.topviec_be.service;

import com.topviec.topviec_be.dto.response.dashboard.ResManagerDashboardDTO;
import com.topviec.topviec_be.dto.response.dashboard.ResOwnerDashboardDTO;
import com.topviec.topviec_be.dto.response.dashboard.ResRecruiterDashboardDTO;
import com.topviec.topviec_be.dto.response.dashboard.ResViewerDashboardDTO;

public interface EmployerDashboardService {
    ResOwnerDashboardDTO getOwnerDashboard(Long companyId);

    ResManagerDashboardDTO getManagerDashboard(Long companyId);

    ResRecruiterDashboardDTO getRecruiterDashboard(Long companyId, Long userId);

    ResViewerDashboardDTO getViewerDashboard(Long companyId);
}
