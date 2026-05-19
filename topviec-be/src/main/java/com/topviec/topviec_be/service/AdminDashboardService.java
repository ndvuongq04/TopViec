package com.topviec.topviec_be.service;

import com.topviec.topviec_be.dto.response.dashboard.ResContentModeratorDashboardDTO;
import com.topviec.topviec_be.dto.response.dashboard.ResFinanceAdminDashboardDTO;
import com.topviec.topviec_be.dto.response.dashboard.ResSuperAdminDashboardDTO;
import com.topviec.topviec_be.dto.response.dashboard.ResSupportAdminDashboardDTO;

public interface AdminDashboardService {
    ResSuperAdminDashboardDTO getSuperAdminDashboard();

    ResContentModeratorDashboardDTO getContentModeratorDashboard();

    ResSupportAdminDashboardDTO getSupportAdminDashboard();

    ResFinanceAdminDashboardDTO getFinanceAdminDashboard();
}
