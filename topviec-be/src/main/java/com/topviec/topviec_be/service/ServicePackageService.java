package com.topviec.topviec_be.service;

import com.topviec.topviec_be.dto.request.ReqServicePackageDTO;
import com.topviec.topviec_be.dto.response.ResServicePackageDTO;
import com.topviec.topviec_be.dto.response.ResultPaginationDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ServicePackageService {
    List<ResServicePackageDTO> getPublicActivePackages();
    ResultPaginationDTO getAllServicePackages(String keyword, Pageable pageable);
    ResServicePackageDTO getServicePackageById(Long id);
    ResServicePackageDTO createServicePackage(ReqServicePackageDTO reqDTO);
    ResServicePackageDTO updateServicePackage(Long id, ReqServicePackageDTO reqDTO);
}
