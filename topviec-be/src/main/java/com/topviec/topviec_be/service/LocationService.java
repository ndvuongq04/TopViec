package com.topviec.topviec_be.service;

import com.topviec.topviec_be.dto.request.ReqLocationDTO;
import com.topviec.topviec_be.dto.response.ResLocationDTO;
import com.topviec.topviec_be.dto.response.ResultPaginationDTO;

import java.util.List;

public interface LocationService {

    ResultPaginationDTO getLocations(String keyword, int page, int size);

    ResLocationDTO getLocationById(Long id);

    List<ResLocationDTO> createLocations(List<ReqLocationDTO> requests);

    ResLocationDTO updateLocation(Long id, ReqLocationDTO request);

    void deleteLocation(Long id);
}