package com.topviec.topviec_be.service;

import com.topviec.topviec_be.dto.request.ReqServiceDTO;
import com.topviec.topviec_be.dto.response.ResAdminServiceStatisticsDTO;
import com.topviec.topviec_be.dto.response.ResServiceDTO;
import com.topviec.topviec_be.dto.response.ResultPaginationDTO;
import com.topviec.topviec_be.enums.services.ServiceCategory;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ServiceCatalogService {
    List<ResServiceDTO> getActiveServices(ServiceCategory category);
    ResultPaginationDTO getAllServices(ServiceCategory category, String keyword, Pageable pageable);
    ResServiceDTO getServiceById(Long id);
    ResServiceDTO createService(ReqServiceDTO reqDTO);
    ResServiceDTO updateService(Long id, ReqServiceDTO reqDTO);

    /** Admin: thống kê tổng quan gói dịch vụ toàn hệ thống */
    ResAdminServiceStatisticsDTO getServiceStatistics();
}
