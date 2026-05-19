package com.topviec.topviec_be.service.impl;

import com.topviec.topviec_be.dto.request.ReqIndustryDTO;
import com.topviec.topviec_be.dto.response.ResIndustryDTO;
import com.topviec.topviec_be.dto.response.ResultPaginationDTO;
import com.topviec.topviec_be.entity.Industry;
import com.topviec.topviec_be.repository.IndustryRepository;
import com.topviec.topviec_be.service.IndustryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IndustryServiceImpl implements IndustryService {

    private final IndustryRepository industryRepository;

    @Override
    public ResultPaginationDTO getIndustries(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Industry> industryPage = industryRepository.searchIndustries(keyword, pageable);

        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(page);
        meta.setPageSize(size);
        meta.setPages(industryPage.getTotalPages());
        meta.setTotals(industryPage.getTotalElements());

        ResultPaginationDTO result = new ResultPaginationDTO();
        result.setMeta(meta);
        result.setResult(industryPage.getContent().stream().map(this::toResponse).toList());

        return result;
    }

    @Override
    public ResIndustryDTO getIndustryById(Long id) {
        Industry industry = industryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Industry not found with id: " + id));
        return toResponse(industry);
    }

    @Override
    public ResIndustryDTO createIndustry(ReqIndustryDTO request) {
        Industry industry = new Industry();
        mapRequestToEntity(request, industry);
        return toResponse(industryRepository.save(industry));
    }

    @Override
    public ResIndustryDTO updateIndustry(Long id, ReqIndustryDTO request) {
        Industry industry = industryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Industry not found with id: " + id));
        mapRequestToEntity(request, industry);
        return toResponse(industryRepository.save(industry));
    }

    @Override
    public void deleteIndustry(Long id) {
        Industry industry = industryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Industry not found with id: " + id));
        industryRepository.delete(industry);
    }

    // ========== PRIVATE HELPERS ==========

    private void mapRequestToEntity(ReqIndustryDTO request, Industry industry) {
        if (request.getParentId() != null) {
            Industry parent = industryRepository.findById(request.getParentId())
                    .orElseThrow(
                            () -> new RuntimeException("Parent industry not found with id: " + request.getParentId()));
            industry.setParent(parent);
        } else {
            industry.setParent(null);
        }
        industry.setName(request.getName());
        industry.setSlug(request.getSlug());
        industry.setIcon(request.getIcon());
        industry.setSortOrder(request.getSortOrder());
        industry.setIsActive(request.getIsActive());
    }

    private ResIndustryDTO toResponse(Industry i) {
        return ResIndustryDTO.builder()
                .id(i.getId())
                .parentId(i.getParent() != null ? i.getParent().getId() : null)
                .name(i.getName())
                .slug(i.getSlug())
                .icon(i.getIcon())
                .sortOrder(i.getSortOrder())
                .isActive(i.getIsActive())
                .build();
    }
}