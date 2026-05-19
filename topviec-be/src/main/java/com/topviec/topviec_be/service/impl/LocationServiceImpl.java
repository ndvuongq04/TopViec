package com.topviec.topviec_be.service.impl;

import com.topviec.topviec_be.dto.request.ReqLocationDTO;
import com.topviec.topviec_be.dto.response.ResLocationDTO;
import com.topviec.topviec_be.dto.response.ResultPaginationDTO;
import com.topviec.topviec_be.entity.Location;
import com.topviec.topviec_be.repository.LocationRepository;
import com.topviec.topviec_be.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;

    @Override
    public ResultPaginationDTO getLocations(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Location> locationPage = locationRepository.searchLocations(keyword, pageable);

        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(page);
        meta.setPageSize(size);
        meta.setPages(locationPage.getTotalPages());
        meta.setTotals(locationPage.getTotalElements());

        ResultPaginationDTO result = new ResultPaginationDTO();
        result.setMeta(meta);
        result.setResult(locationPage.getContent().stream().map(this::toResponse).toList());

        return result;
    }

    @Override
    public ResLocationDTO getLocationById(Long id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Location not found with id: " + id));
        return toResponse(location);
    }

    @Override
    public List<ResLocationDTO> createLocations(List<ReqLocationDTO> requests) {
        List<Location> locations = requests.stream()
                .map(request -> {
                    Location location = new Location();
                    mapRequestToEntity(request, location);
                    return location;
                })
                .toList();
        return locationRepository.saveAll(locations)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public ResLocationDTO updateLocation(Long id, ReqLocationDTO request) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Location not found with id: " + id));
        mapRequestToEntity(request, location);
        return toResponse(locationRepository.save(location));
    }

    @Override
    public void deleteLocation(Long id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Location not found with id: " + id));
        locationRepository.delete(location);
    }

    // ========== PRIVATE HELPERS ==========

    private void mapRequestToEntity(ReqLocationDTO request, Location location) {
        location.setName(request.getName());
        location.setCode(request.getCode());
        location.setSortOrder(request.getSortOrder());
    }

    private ResLocationDTO toResponse(Location l) {
        return ResLocationDTO.builder()
                .id(l.getId())
                .name(l.getName())
                .code(l.getCode())
                .sortOrder(l.getSortOrder())
                .build();
    }
}