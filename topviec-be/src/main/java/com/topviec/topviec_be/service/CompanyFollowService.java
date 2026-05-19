package com.topviec.topviec_be.service;

import com.topviec.topviec_be.dto.response.ResultPaginationDTO;
import org.springframework.data.domain.Pageable;

public interface CompanyFollowService {
    void followCompany(Long userId, Long companyId);
    void unfollowCompany(Long userId, Long companyId);
    boolean checkFollowStatus(Long userId, Long companyId);
    ResultPaginationDTO getFollowedCompanies(Long userId, Pageable pageable);
}
