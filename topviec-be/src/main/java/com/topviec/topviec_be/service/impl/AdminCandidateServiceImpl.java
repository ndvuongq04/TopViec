package com.topviec.topviec_be.service.impl;

import com.topviec.topviec_be.dto.response.ResAdminCandidateDTO;
import com.topviec.topviec_be.dto.response.ResAdminCandidateDetailDTO;
import com.topviec.topviec_be.dto.response.ResAdminCandidateStatisticsDTO;
import com.topviec.topviec_be.dto.response.ResultPaginationDTO;
import com.topviec.topviec_be.entity.CandidateProfile;
import com.topviec.topviec_be.entity.User;
import com.topviec.topviec_be.enums.users.UserStatus;
import com.topviec.topviec_be.exception.AppException;
import com.topviec.topviec_be.repository.CandidateProfileRepository;
import com.topviec.topviec_be.repository.LocationRepository;
import com.topviec.topviec_be.repository.UserRepository;
import com.topviec.topviec_be.repository.CvsRepository;
import com.topviec.topviec_be.repository.ApplicationRepository;
import com.topviec.topviec_be.repository.CompanyFollowRepository;
import com.topviec.topviec_be.repository.SavedJobRepository;
import com.topviec.topviec_be.entity.Location;
import com.topviec.topviec_be.service.AdminCandidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.topviec.topviec_be.service.CvService;
import com.topviec.topviec_be.service.ApplicationService;
import com.topviec.topviec_be.service.CompanyFollowService;
import com.topviec.topviec_be.service.SavedJobService;
import com.topviec.topviec_be.dto.response.ResCompanyFollowDTO;
import com.topviec.topviec_be.dto.response.ResCvDTO;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminCandidateServiceImpl implements AdminCandidateService {

    private final CandidateProfileRepository candidateProfileRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final CvService cvService;
    private final ApplicationService applicationService;
    private final CompanyFollowService companyFollowService;
    private final SavedJobService savedJobService;
    private final CvsRepository cvsRepository;
    private final ApplicationRepository applicationRepository;
    private final CompanyFollowRepository companyFollowRepository;
    private final SavedJobRepository savedJobRepository;

    @Override
    public ResultPaginationDTO getCandidates(String statusStr, String keyword, Pageable pageable) {
        UserStatus status = null;
        if (statusStr != null && !statusStr.trim().isEmpty()) {
            status = UserStatus.fromValue(statusStr);
        }

        String searchKeyword = null;
        Long userId = null;

        if (keyword != null && !keyword.trim().isEmpty()) {
            searchKeyword = keyword.trim();
            try {
                userId = Long.parseLong(searchKeyword);
            } catch (NumberFormatException e) {
                // Ignore, it's just not an ID
            }
        }

        Page<Object[]> pageResult = candidateProfileRepository.searchAdminCandidates(
                status,
                searchKeyword,
                userId,
                pageable);

        List<ResAdminCandidateDTO> dtos = pageResult.getContent().stream().map(obj -> {
            CandidateProfile cp = (CandidateProfile) obj[0];
            User u = (User) obj[1];

            return ResAdminCandidateDTO.builder()
                    .id(u.getId())
                    .fullName(cp.getFullName())
                    .email(u.getEmail())
                    .phoneDisplay(cp.getPhoneDisplay())
                    .status(u.getStatus().getValue())
                    .avatarUrl(cp.getAvatarUrl())
                    .jobSeekingStatus(cp.getJobSeekingStatus())
                    .createdAt(cp.getCreatedAt())
                    .build();
        }).collect(Collectors.toList());

        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber());
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageResult.getTotalPages());
        meta.setTotals(pageResult.getTotalElements());

        ResultPaginationDTO result = new ResultPaginationDTO();
        result.setMeta(meta);
        result.setResult(dtos);

        return result;
    }

    @Override
    public ResAdminCandidateDetailDTO getCandidateDetail(Long userId) {
        User u = userRepository.findById(userId)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy tài khoản người dùng"));

        CandidateProfile cp = candidateProfileRepository.findByUserId(userId)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy hồ sơ ứng viên"));

        return ResAdminCandidateDetailDTO.builder()
                .id(u.getId())
                .fullName(cp.getFullName())
                .email(u.getEmail())
                .phoneDisplay(cp.getPhoneDisplay())
                .status(u.getStatus().getValue())
                .avatarUrl(cp.getAvatarUrl())
                .jobSeekingStatus(cp.getJobSeekingStatus())
                .dateOfBirth(cp.getDateOfBirth())
                .gender(cp.getGender())
                .bio(cp.getBio())
                .linkedinUrl(cp.getLinkedinUrl())
                .githubUrl(cp.getGithubUrl())
                .personalWebsite(cp.getPersonalWebsite())
                .expectedSalaryMin(cp.getExpectedSalaryMin())
                .expectedSalaryMax(cp.getExpectedSalaryMax())
                .salaryNegotiable(cp.getSalaryNegotiable())
                .preferredJobTitle(cp.getPreferredJobTitle())
                .preferredWorkType(cp.getPreferredWorkType())
                .preferredLocationId(cp.getPreferredLocationId())
                .preferredLocationName(cp.getPreferredLocationId() != null
                        ? locationRepository.findById(Long.valueOf(cp.getPreferredLocationId()))
                                .map(Location::getName).orElse(null)
                        : null)
                .profileCompletionPct(cp.getProfileCompletionPct())
                .isCvPublic(cp.getCvPublic())
                .createdAt(cp.getCreatedAt())
                .build();
    }

    @Override
    public ResultPaginationDTO getCandidateCvs(Long userId, Pageable pageable) {
        List<ResCvDTO> allCvs = cvService.getMyCvs(userId);

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allCvs.size());
        List<ResCvDTO> pagedCvs = java.util.Collections.emptyList();

        if (start < allCvs.size()) {
            pagedCvs = allCvs.subList(start, end);
        }

        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber());
        meta.setPageSize(pageable.getPageSize());
        meta.setPages((int) Math.ceil((double) allCvs.size() / pageable.getPageSize()));
        meta.setTotals(allCvs.size());

        ResultPaginationDTO result = new ResultPaginationDTO();
        result.setMeta(meta);
        result.setResult(pagedCvs);

        return result;
    }

    @Override
    public ResultPaginationDTO getCandidateApplications(Long userId, Pageable pageable) {
        return applicationService.getMyApplications(userId, null, pageable);
    }

    @Override
    public ResultPaginationDTO getCandidateFollowedCompanies(Long userId, Pageable pageable) {
        return companyFollowService.getFollowedCompanies(userId, pageable);
    }

    @Override
    public ResultPaginationDTO getCandidateSavedJobs(Long userId, Pageable pageable) {
        return savedJobService.getSavedJobs(userId, pageable.getPageNumber(), pageable.getPageSize());
    }

    @Override
    public ResAdminCandidateStatisticsDTO getCandidateStatistics(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy tài khoản người dùng"));

        long totalCvs = cvsRepository.countByUserId(userId);
        long totalApplications = applicationRepository.countByCandidateUserIdAndDeletedAtIsNull(userId);
        long totalFollowedCompanies = companyFollowRepository.countByUserId(userId);
        long totalSavedJobs = savedJobRepository.countByUserId(userId);

        return ResAdminCandidateStatisticsDTO.builder()
                .totalCvs(totalCvs)
                .totalApplications(totalApplications)
                .totalFollowedCompanies(totalFollowedCompanies)
                .totalSavedJobs(totalSavedJobs)
                .build();
    }

    @Override
    public String toggleCandidateStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy tài khoản người dùng"));

        UserStatus current = user.getStatus();

        if (current == UserStatus.ACTIVE) {
            user.setStatus(UserStatus.LOCKED_PERM);
        } else if (current == UserStatus.LOCKED_PERM) {
            user.setStatus(UserStatus.ACTIVE);
        } else {
            throw AppException.badRequest("Không thể thay đổi trạng thái tài khoản đang ở trạng thái: " + current.getValue());
        }

        userRepository.save(user);
        return user.getStatus().getValue();
    }
}
