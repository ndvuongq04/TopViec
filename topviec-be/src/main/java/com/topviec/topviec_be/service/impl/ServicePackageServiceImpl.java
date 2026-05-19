package com.topviec.topviec_be.service.impl;

import com.topviec.topviec_be.dto.request.ReqServicePackageDTO;
import com.topviec.topviec_be.dto.response.ResServicePackageDTO;
import com.topviec.topviec_be.dto.response.ResServicePackageDetailDTO;
import com.topviec.topviec_be.dto.response.ResultPaginationDTO;
import com.topviec.topviec_be.entity.ServicePackage;
import com.topviec.topviec_be.entity.ServicePackageDetail;
import com.topviec.topviec_be.entity.Services;
import com.topviec.topviec_be.exception.AppException;
import com.topviec.topviec_be.repository.ServicePackageDetailRepository;
import com.topviec.topviec_be.repository.ServicePackageRepository;
import com.topviec.topviec_be.repository.ServiceRepository;
import com.topviec.topviec_be.service.ServicePackageService;
import com.topviec.topviec_be.util.ChangeTracker;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServicePackageServiceImpl implements ServicePackageService {

    private final ServicePackageRepository servicePackageRepository;
    private final ServicePackageDetailRepository servicePackageDetailRepository;
    private final ServiceRepository serviceRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ResServicePackageDTO> getPublicActivePackages() {
        return servicePackageRepository.findByIsActiveTrueOrderBySortOrderAsc().stream()
                .map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ResultPaginationDTO getAllServicePackages(String keyword, Pageable pageable) {
        Page<ServicePackage> page = servicePackageRepository.searchAll(keyword, pageable);

        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber());
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(page.getTotalPages());
        meta.setTotals(page.getTotalElements());

        List<ResServicePackageDTO> results = page.getContent().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        ResultPaginationDTO response = new ResultPaginationDTO();
        response.setMeta(meta);
        response.setResult(results);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public ResServicePackageDTO getServicePackageById(Long id) {
        ServicePackage servicePackage = servicePackageRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy gói dịch vụ với ID: " + id));
        return mapToDTO(servicePackage);
    }

    @Override
    @Transactional
    public ResServicePackageDTO createServicePackage(ReqServicePackageDTO reqDTO) {
        if (servicePackageRepository.existsByCode(reqDTO.getCode())) {
            throw AppException.badRequest("Mã gói dịch vụ đã tồn tại, vui lòng chọn mã khác.");
        }

        ServicePackage servicePackage = ServicePackage.builder()
                .name(reqDTO.getName())
                .code(reqDTO.getCode())
                .billingCycle(reqDTO.getBillingCycle())
                .price(reqDTO.getPrice())
                .description(reqDTO.getDescription())
                .isActive(reqDTO.getIsActive() != null ? reqDTO.getIsActive() : true)
                .sortOrder(reqDTO.getSortOrder())
                .build();

        ServicePackage saved = servicePackageRepository.save(servicePackage);

        if (reqDTO.getDetails() != null && !reqDTO.getDetails().isEmpty()) {
            saveDetails(saved, reqDTO.getDetails());
        }

        return mapToDTO(servicePackageRepository.findById(saved.getId()).orElse(saved));
    }

    @Override
    @Transactional
    public ResServicePackageDTO updateServicePackage(Long id, ReqServicePackageDTO reqDTO) {
        ServicePackage servicePackage = servicePackageRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy gói dịch vụ với ID: " + id));

        if (servicePackageRepository.existsByCodeAndIdNot(reqDTO.getCode(), id)) {
            throw AppException.badRequest("Mã gói dịch vụ đã tồn tại, vui lòng chọn mã khác.");
        }

        // CDC: Snapshot trước khi sửa
        ChangeTracker tracker = ChangeTracker.of(servicePackage);

        servicePackage.setName(reqDTO.getName());
        servicePackage.setCode(reqDTO.getCode());
        servicePackage.setBillingCycle(reqDTO.getBillingCycle());
        servicePackage.setPrice(reqDTO.getPrice());
        servicePackage.setDescription(reqDTO.getDescription());
        if (reqDTO.getIsActive() != null) {
            servicePackage.setIsActive(reqDTO.getIsActive());
        }
        if (reqDTO.getSortOrder() != null) {
            servicePackage.setSortOrder(reqDTO.getSortOrder());
        }

        ServicePackage saved = servicePackageRepository.save(servicePackage);

        if (reqDTO.getDetails() != null) {
            if (saved.getDetails() != null) {
                saved.getDetails().clear();
            }
            servicePackageDetailRepository.deleteByServicePackageId(saved.getId());
            servicePackageDetailRepository.flush();
            
            if (!reqDTO.getDetails().isEmpty()) {
                saveDetails(saved, reqDTO.getDetails());
            }
        }

        // CDC: So sánh + ghi vào log context
        tracker.compare(saved).apply();

        return mapToDTO(servicePackageRepository.findById(saved.getId()).orElse(saved));
    }

    private void saveDetails(ServicePackage pkg, List<ReqServicePackageDTO.DetailItem> items) {
        for (ReqServicePackageDTO.DetailItem item : items) {
            Services service = serviceRepository.findById(item.getServiceId())
                    .orElseThrow(() -> AppException.notFound("Không tìm thấy dịch vụ với ID: " + item.getServiceId()));

            ServicePackageDetail detail = ServicePackageDetail.builder()
                    .servicePackageId(pkg.getId())
                    .serviceId(service.getId())
                    .quantity(item.getQuantity())
                    .build();

            servicePackageDetailRepository.save(detail);
        }
    }

    private ResServicePackageDTO mapToDTO(ServicePackage entity) {
        List<ServicePackageDetail> rawDetails = entity.getDetails();
        if (rawDetails == null) {
            rawDetails = servicePackageDetailRepository.findByServicePackageId(entity.getId());
        }

        List<ResServicePackageDetailDTO> detailDTOs = rawDetails.stream()
                .map(d -> {
                    Services svc = d.getService();
                    if (svc == null) {
                        svc = serviceRepository.findById(d.getServiceId()).orElse(null);
                    }
                    return ResServicePackageDetailDTO.builder()
                            .id(d.getId())
                            .serviceId(d.getServiceId())
                            .serviceCode(svc != null ? svc.getCode() : null)
                            .serviceName(svc != null ? svc.getName() : null)
                            .serviceCategory(svc != null ? svc.getCategory() : null)
                            .serviceCategoryName(svc != null && svc.getCategory() != null ? svc.getCategory().getValue() : null)
                            .serviceUnit(svc != null ? svc.getUnit() : null)
                            .quantity(d.getQuantity())
                            .build();
                })
                .collect(Collectors.toList());

        return ResServicePackageDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .code(entity.getCode())
                .billingCycle(entity.getBillingCycle())
                .price(entity.getPrice())
                .description(entity.getDescription())
                .isActive(entity.getIsActive())
                .sortOrder(entity.getSortOrder())
                .details(detailDTOs)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
