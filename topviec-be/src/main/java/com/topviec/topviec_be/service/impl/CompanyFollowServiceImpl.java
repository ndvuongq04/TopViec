package com.topviec.topviec_be.service.impl;

import com.topviec.topviec_be.dto.response.ResCompanyDTO;
import com.topviec.topviec_be.dto.response.ResCompanyFollowDTO;
import com.topviec.topviec_be.dto.response.ResultPaginationDTO;
import com.topviec.topviec_be.entity.Company;
import com.topviec.topviec_be.entity.CompanyFollow;
import com.topviec.topviec_be.enums.company.CompanySize;
import com.topviec.topviec_be.enums.company.CompanyStatus;
import com.topviec.topviec_be.enums.company.VerificationStatus;
import com.topviec.topviec_be.exception.AppException;
import com.topviec.topviec_be.repository.CompanyFollowRepository;
import com.topviec.topviec_be.repository.CompanyRepository;
import com.topviec.topviec_be.repository.IndustryRepository;
import com.topviec.topviec_be.entity.Industry;
import com.topviec.topviec_be.service.CompanyFollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyFollowServiceImpl implements CompanyFollowService {

    private final CompanyFollowRepository companyFollowRepository;
    private final CompanyRepository companyRepository;
    private final IndustryRepository industryRepository;

    // -------------------------------------------------------------------------
    // Candidate — Follow / Unfollow Actions
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public void followCompany(Long userId, Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> AppException.notFound("Công ty không tồn tại"));

        if (!"active".equals(company.getStatus())) {
            throw AppException.badRequest("Công ty không còn hoạt động");
        }

        if (companyFollowRepository.existsByUserIdAndCompanyId(userId, companyId)) {
            // Đã theo dõi rồi, bỏ qua
            return;
        }

        CompanyFollow follow = CompanyFollow.builder()
                .userId(userId)
                .companyId(companyId)
                // followedAt sẽ được tự động gán ở @PrePersist
                .build();

        companyFollowRepository.save(follow);
    }

    @Override
    @Transactional
    public void unfollowCompany(Long userId, Long companyId) {
        if (!companyRepository.existsById(companyId)) {
            throw AppException.notFound("Công ty không tồn tại");
        }

        if (!companyFollowRepository.existsByUserIdAndCompanyId(userId, companyId)) {
            throw AppException.badRequest("Bạn chưa theo dõi công ty này");
        }

        companyFollowRepository.deleteByUserIdAndCompanyId(userId, companyId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkFollowStatus(Long userId, Long companyId) {
        return companyFollowRepository.existsByUserIdAndCompanyId(userId, companyId);
    }

    // -------------------------------------------------------------------------
    // Candidate — GET
    // -------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public ResultPaginationDTO getFollowedCompanies(Long userId, Pageable pageable) {
        Page<CompanyFollow> page = companyFollowRepository.findAllByUserId(userId, pageable);

        // Fetch all companies related to this page in a batch
        List<Long> companyIds = page.getContent().stream()
                .map(CompanyFollow::getCompanyId)
                .toList();

        List<Company> companies = companyIds.isEmpty() ? List.of() : companyRepository.findAllById(companyIds);
        Map<Long, Company> companyMap = companies.stream()
                .collect(Collectors.toMap(Company::getId, c -> c));

        // Fetch all industries related to these companies in a batch
        List<Long> industryIds = companies.stream()
                .map(Company::getIndustryId)
                .filter(java.util.Objects::nonNull)
                .distinct()
                .toList();
        Map<Long, String> industryMap = industryIds.isEmpty() ? Map.of()
                : industryRepository.findAllById(industryIds).stream()
                        .collect(Collectors.toMap(Industry::getId, Industry::getName));

        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber());
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(page.getTotalPages());
        meta.setTotals(page.getTotalElements());

        ResultPaginationDTO result = new ResultPaginationDTO();
        result.setMeta(meta);
        result.setResult(page.getContent().stream()
                .filter(cf -> companyMap.containsKey(cf.getCompanyId())) // bỏ qua nếu company đã bị xóa
                .map(cf -> {
                    Company c = companyMap.get(cf.getCompanyId());
                    String industryName = industryMap.get(c.getIndustryId());
                    return toResponse(cf, c, industryName);
                })
                .toList());
        return result;
    }

    // -------------------------------------------------------------------------
    // Helper
    // -------------------------------------------------------------------------

    private ResCompanyFollowDTO toResponse(CompanyFollow cf, Company c, String industryName) {
        ResCompanyDTO companyDTO = ResCompanyDTO.builder()
                .id(c.getId())
                .slug(c.getSlug())
                .name(c.getName())
                .logoUrl(c.getLogoUrl())
                .industryId(c.getIndustryId())
                .industryName(industryName)
                .companySize(c.getCompanySize() != null ? CompanySize.fromValue(c.getCompanySize()) : null)
                .provinceId(c.getProvinceId())
                .address(c.getAddress())
                .status(c.getStatus() != null ? CompanyStatus.fromValue(c.getStatus()) : null)
                .verificationStatus(
                        c.getVerificationStatus() != null ? VerificationStatus.fromValue(c.getVerificationStatus())
                                : null)
                .build();

        return ResCompanyFollowDTO.builder()
                .id(cf.getId())
                .followedAt(cf.getFollowedAt())
                .company(companyDTO)
                .build();
    }
}