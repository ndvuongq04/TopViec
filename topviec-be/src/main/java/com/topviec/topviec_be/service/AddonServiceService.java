package com.topviec.topviec_be.service;

import com.topviec.topviec_be.dto.request.ReqAddonServiceDTO;
import com.topviec.topviec_be.dto.response.ResAddonServiceDTO;
import com.topviec.topviec_be.dto.response.ResultPaginationDTO;
import com.topviec.topviec_be.enums.services.ServiceCategory;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AddonServiceService {
    List<ResAddonServiceDTO> getActiveAddonServices(ServiceCategory category);
    ResultPaginationDTO getAllAddonServices(ServiceCategory category, String keyword, Pageable pageable);
    ResAddonServiceDTO getAddonServiceById(Long id);
    ResAddonServiceDTO createAddonService(ReqAddonServiceDTO reqDTO);
    ResAddonServiceDTO updateAddonService(Long id, ReqAddonServiceDTO reqDTO);
}
