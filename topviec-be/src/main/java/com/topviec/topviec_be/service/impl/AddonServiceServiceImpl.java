package com.topviec.topviec_be.service.impl;

import com.topviec.topviec_be.dto.request.ReqAddonServiceDTO;
import com.topviec.topviec_be.dto.response.ResAddonServiceDTO;
import com.topviec.topviec_be.dto.response.ResultPaginationDTO;
import com.topviec.topviec_be.entity.AddonService;
import com.topviec.topviec_be.entity.Services;
import com.topviec.topviec_be.enums.services.ServiceCategory;
import com.topviec.topviec_be.exception.AppException;
import com.topviec.topviec_be.repository.AddonServiceRepository;
import com.topviec.topviec_be.repository.ServiceRepository;
import com.topviec.topviec_be.service.AddonServiceService;
import com.topviec.topviec_be.util.ChangeTracker;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddonServiceServiceImpl implements AddonServiceService {

    private final AddonServiceRepository addonServiceRepository;
    private final ServiceRepository serviceRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ResAddonServiceDTO> getActiveAddonServices(ServiceCategory category) {
        List<AddonService> list = category != null
                ? addonServiceRepository.findByIsActiveTrueAndServiceCategory(category)
                : addonServiceRepository.findByIsActiveTrue();
        return list.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ResultPaginationDTO getAllAddonServices(ServiceCategory category, String keyword, Pageable pageable) {
        Page<AddonService> page = addonServiceRepository.searchAll(category, keyword, pageable);

        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber());
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(page.getTotalPages());
        meta.setTotals(page.getTotalElements());

        List<ResAddonServiceDTO> results = page.getContent().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        ResultPaginationDTO response = new ResultPaginationDTO();
        response.setMeta(meta);
        response.setResult(results);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public ResAddonServiceDTO getAddonServiceById(Long id) {
        AddonService addonService = addonServiceRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy dịch vụ lẻ với ID: " + id));
        return mapToDTO(addonService);
    }

    @Override
    @Transactional
    public ResAddonServiceDTO createAddonService(ReqAddonServiceDTO reqDTO) {
        if (addonServiceRepository.existsByCode(reqDTO.getCode())) {
            throw AppException.badRequest("Mã dịch vụ lẻ đã tồn tại, vui lòng chọn mã khác.");
        }

        Services service = serviceRepository.findById(reqDTO.getServiceId())
                .orElseThrow(() -> AppException.notFound("Không tìm thấy dịch vụ với ID: " + reqDTO.getServiceId()));

        AddonService addonService = AddonService.builder()
                .serviceId(service.getId())
                .name(reqDTO.getName())
                .code(reqDTO.getCode())
                .quantity(reqDTO.getQuantity())
                .durationDays(reqDTO.getDurationDays())
                .price(reqDTO.getPrice())
                .description(reqDTO.getDescription())
                .isActive(reqDTO.getIsActive() != null ? reqDTO.getIsActive() : true)
                .build();

        return mapToDTO(addonServiceRepository.save(addonService));
    }

    @Override
    @Transactional
    public ResAddonServiceDTO updateAddonService(Long id, ReqAddonServiceDTO reqDTO) {
        AddonService addonService = addonServiceRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy dịch vụ lẻ với ID: " + id));

        if (addonServiceRepository.existsByCodeAndIdNot(reqDTO.getCode(), id)) {
            throw AppException.badRequest("Mã dịch vụ lẻ đã tồn tại, vui lòng chọn mã khác.");
        }

        Services service = serviceRepository.findById(reqDTO.getServiceId())
                .orElseThrow(() -> AppException.notFound("Không tìm thấy dịch vụ với ID: " + reqDTO.getServiceId()));

        // CDC: Snapshot trước khi sửa
        ChangeTracker tracker = ChangeTracker.of(addonService);

        addonService.setServiceId(service.getId());
        addonService.setName(reqDTO.getName());
        addonService.setCode(reqDTO.getCode());
        addonService.setQuantity(reqDTO.getQuantity());
        addonService.setDurationDays(reqDTO.getDurationDays());
        addonService.setPrice(reqDTO.getPrice());
        addonService.setDescription(reqDTO.getDescription());
        if (reqDTO.getIsActive() != null) {
            addonService.setIsActive(reqDTO.getIsActive());
        }

        AddonService saved = addonServiceRepository.save(addonService);

        // CDC: So sánh + ghi vào log context
        tracker.compare(saved).apply();

        return mapToDTO(saved);
    }

    public ResAddonServiceDTO mapToDTO(AddonService entity) {
        Services svc = entity.getService();
        if (svc == null) {
            svc = serviceRepository.findById(entity.getServiceId()).orElse(null);
        }
        return ResAddonServiceDTO.builder()
                .id(entity.getId())
                .serviceId(entity.getServiceId())
                .serviceCode(svc != null ? svc.getCode() : null)
                .serviceName(svc != null ? svc.getName() : null)
                .serviceCategory(svc != null ? svc.getCategory() : null)
                .serviceCategoryName(svc != null && svc.getCategory() != null ? svc.getCategory().getValue() : null)
                .name(entity.getName())
                .code(entity.getCode())
                .quantity(entity.getQuantity())
                .durationDays(entity.getDurationDays())
                .price(entity.getPrice())
                .description(entity.getDescription())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
