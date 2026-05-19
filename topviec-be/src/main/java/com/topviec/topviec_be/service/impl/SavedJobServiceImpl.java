package com.topviec.topviec_be.service.impl;

import com.topviec.topviec_be.dto.response.ResSavedJobDTO;
import com.topviec.topviec_be.dto.response.ResJobPostingSummary;
import com.topviec.topviec_be.dto.response.ResultPaginationDTO;
import com.topviec.topviec_be.entity.Company;
import com.topviec.topviec_be.entity.Industry;
import com.topviec.topviec_be.entity.JobPosting;
import com.topviec.topviec_be.entity.Level;
import com.topviec.topviec_be.entity.SavedJob;
import com.topviec.topviec_be.entity.User;
import com.topviec.topviec_be.exception.AppException;
import com.topviec.topviec_be.repository.CompanyRepository;
import com.topviec.topviec_be.repository.IndustryRepository;
import com.topviec.topviec_be.repository.JobPostLocationRepository;
import com.topviec.topviec_be.repository.JobPostingRepository;
import com.topviec.topviec_be.repository.LevelRepository;
import com.topviec.topviec_be.repository.SavedJobRepository;
import com.topviec.topviec_be.service.SavedJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SavedJobServiceImpl implements SavedJobService {

        private final SavedJobRepository savedJobRepository;
        private final JobPostingRepository jobPostingRepository;
        private final CompanyRepository companyRepository;
        private final IndustryRepository industryRepository;
        private final LevelRepository levelRepository;
        private final JobPostLocationRepository jobPostLocationRepository;

        @Override
        @Transactional
        public ResSavedJobDTO toggleSave(Long userId, Long jobPostId) {
                // Kiểm tra job tồn tại
                JobPosting jobPosting = jobPostingRepository.findByIdAndDeletedAtIsNull(jobPostId)
                                .orElseThrow(() -> AppException.notFound("Không tìm thấy tin tuyển dụng"));

                // Nếu đã lưu → bỏ lưu
                savedJobRepository.findByUserIdAndJobPostId(userId, jobPostId)
                                .ifPresent(savedJobRepository::delete);

                User user = new User();
                user.setId(userId);

                // Nếu chưa lưu → lưu
                SavedJob saved = savedJobRepository.save(SavedJob.builder()
                                .user(user)
                                .jobPost(jobPosting)
                                .build());

                return toResponse(saved, jobPosting);
        }

        @Override
        public boolean isSaved(Long userId, Long jobPostId) {
                return savedJobRepository.existsByUserIdAndJobPostId(userId, jobPostId);
        }

        @Override
        @Transactional(readOnly = true)
        public ResultPaginationDTO getSavedJobs(Long userId, int page, int size) {
                Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "savedAt"));
                Page<SavedJob> savedPage = savedJobRepository.findByUserId(userId, pageable);

                List<SavedJob> savedJobs = savedPage.getContent();
                List<Long> jobPostIds = savedJobs.stream().map(savedJob -> savedJob.getJobPost().getId()).toList();

                // Batch query jobs
                Map<Long, JobPosting> jobMap = jobPostingRepository.findAllById(jobPostIds)
                                .stream().collect(Collectors.toMap(JobPosting::getId, j -> j));

                // Batch query company/industry/level
                Map<Long, Company> companyMap = companyRepository
                                .findAllById(jobMap.values().stream().map(JobPosting::getCompanyId).distinct().toList())
                                .stream().collect(Collectors.toMap(Company::getId, c -> c));

                Map<Long, Industry> industryMap = industryRepository
                                .findAllById(jobMap.values().stream().map(JobPosting::getIndustryId).distinct()
                                                .toList())
                                .stream().collect(Collectors.toMap(Industry::getId, i -> i));

                Map<Long, Level> levelMap = levelRepository
                                .findAllById(jobMap.values().stream().map(JobPosting::getLevelId).distinct().toList())
                                .stream().collect(Collectors.toMap(Level::getId, l -> l));

                // Batch load locations kèm province (1 query, tránh N+1)
                Map<Long, List<ResJobPostingSummary.LocationDTO>> locationMap = new HashMap<>();
                if (!jobPostIds.isEmpty()) {
                        jobPostLocationRepository.findByJobPostIdInWithProvince(jobPostIds).forEach(loc -> {
                                locationMap.computeIfAbsent(loc.getJobPostId(), k -> new ArrayList<>())
                                                .add(ResJobPostingSummary.LocationDTO.builder()
                                                                .id(loc.getProvinceId())
                                                                .name(loc.getProvince() != null ? loc.getProvince().getName() : null)
                                                                .addressDetail(loc.getAddressDetail())
                                                                .isRemote(loc.getIsRemote())
                                                                .build());
                        });
                }

                ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
                meta.setPage(page);
                meta.setPageSize(size);
                meta.setPages(savedPage.getTotalPages());
                meta.setTotals(savedPage.getTotalElements());

                ResultPaginationDTO result = new ResultPaginationDTO();
                result.setMeta(meta);
                result.setResult(savedJobs.stream()
                                .map(s -> {
                                        JobPosting j = jobMap.get(s.getJobPost().getId());
                                        return toResponse(s, j, companyMap, industryMap, levelMap, locationMap);
                                })
                                .toList());

                return result;
        }

        @Override
        @Transactional
        public void unsave(Long userId, Long jobPostId) {
                SavedJob savedJob = savedJobRepository.findByUserIdAndJobPostId(userId, jobPostId)
                                .orElseThrow(() -> AppException.notFound("Tin chưa được lưu"));
                savedJobRepository.delete(savedJob);
        }

        // ========== PRIVATE HELPERS ==========

        private ResSavedJobDTO toResponse(SavedJob s, JobPosting j) {
                Company company = companyRepository.findById(j.getCompanyId()).orElse(null);
                Industry industry = industryRepository.findById(j.getIndustryId()).orElse(null);
                Level level = levelRepository.findById(j.getLevelId()).orElse(null);
                List<ResJobPostingSummary.LocationDTO> locations = jobPostLocationRepository
                                .findByJobPostIdWithProvince(j.getId()).stream()
                                .map(loc -> ResJobPostingSummary.LocationDTO.builder()
                                                .id(loc.getProvinceId())
                                                .name(loc.getProvince() != null ? loc.getProvince().getName() : null)
                                                .addressDetail(loc.getAddressDetail())
                                                .isRemote(loc.getIsRemote())
                                                .build())
                                .toList();
                return buildResponse(s, j, company, industry, level, locations);
        }

        private ResSavedJobDTO toResponse(SavedJob s, JobPosting j,
                        Map<Long, Company> companyMap,
                        Map<Long, Industry> industryMap,
                        Map<Long, Level> levelMap,
                        Map<Long, List<ResJobPostingSummary.LocationDTO>> locationMap) {
                return buildResponse(s, j,
                                companyMap.get(j.getCompanyId()),
                                industryMap.get(j.getIndustryId()),
                                levelMap.get(j.getLevelId()),
                                locationMap.getOrDefault(j.getId(), List.of()));
        }

        private ResSavedJobDTO buildResponse(SavedJob s, JobPosting j,
                        Company company, Industry industry, Level level,
                        List<ResJobPostingSummary.LocationDTO> locations) {
                ResJobPostingSummary summary = ResJobPostingSummary.builder()
                                .id(j.getId())
                                .title(j.getTitle())
                                .slug(j.getSlug())
                                .company(company == null ? null
                                                : ResJobPostingSummary.CompanyDTO.builder()
                                                                .id(company.getId())
                                                                .name(company.getName())
                                                                .slug(company.getSlug())
                                                                .logoUrl(company.getLogoUrl())
                                                                .address(company.getAddress())
                                                                .isTopEmployer(company.getIsTopEmployer())
                                                                .isBrandVerified(company.getIsBrandVerified())
                                                                .build())
                                .industry(industry == null ? null
                                                : ResJobPostingSummary.IndustryDTO.builder()
                                                                .id(industry.getId())
                                                                .name(industry.getName())
                                                                .build())
                                .level(level == null ? null
                                                : ResJobPostingSummary.LevelDTO.builder()
                                                                .id(level.getId())
                                                                .name(level.getName())
                                                                .build())
                                .workType(j.getWorkType())
                                .status(j.getStatus())
                                .salaryMin(j.getSalaryMin())
                                .salaryMax(j.getSalaryMax())
                                .salaryNegotiable(j.getSalaryNegotiable())
                                .isFeatured(j.getIsFeatured())
                                .isUrgent(j.getIsUrgent())
                                .isHot(j.getIsHot())
                                .viewCount(j.getViewCount())
                                .deadline(j.getDeadline())
                                .publishedAt(j.getPublishedAt())
                                .createdAt(j.getCreatedAt())
                                .locations(locations)
                                .build();

                return ResSavedJobDTO.builder()
                                .id(s.getId())
                                .jobPostId(s.getJobPost().getId())
                                .savedAt(s.getSavedAt())
                                .jobPosting(summary)
                                .build();
        }
}