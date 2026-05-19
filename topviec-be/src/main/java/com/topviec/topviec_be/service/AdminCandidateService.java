package com.topviec.topviec_be.service;

import com.topviec.topviec_be.dto.response.ResAdminCandidateDetailDTO;
import com.topviec.topviec_be.dto.response.ResAdminCandidateStatisticsDTO;
import com.topviec.topviec_be.dto.response.ResultPaginationDTO;
import org.springframework.data.domain.Pageable;

public interface AdminCandidateService {

    ResultPaginationDTO getCandidates(String status, String keyword, Pageable pageable);

    ResAdminCandidateDetailDTO getCandidateDetail(Long userId);

    ResultPaginationDTO getCandidateCvs(Long userId, Pageable pageable);

    ResultPaginationDTO getCandidateApplications(Long userId, Pageable pageable);

    ResultPaginationDTO getCandidateFollowedCompanies(Long userId, Pageable pageable);

    ResultPaginationDTO getCandidateSavedJobs(Long userId, Pageable pageable);

    ResAdminCandidateStatisticsDTO getCandidateStatistics(Long userId);

    String toggleCandidateStatus(Long userId);
}
