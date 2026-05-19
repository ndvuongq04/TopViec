package com.topviec.topviec_be.controller;

import com.topviec.topviec_be.dto.request.ReqLocationDTO;
import com.topviec.topviec_be.dto.response.ResLocationDTO;
import com.topviec.topviec_be.dto.response.ResultPaginationDTO;
import com.topviec.topviec_be.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @GetMapping
    public ResponseEntity<ResultPaginationDTO> getLocations(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(locationService.getLocations(keyword, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResLocationDTO> getLocationById(@PathVariable Long id) {
        return ResponseEntity.ok(locationService.getLocationById(id));
    }

    @PostMapping
    public ResponseEntity<List<ResLocationDTO>> createLocations(@RequestBody List<ReqLocationDTO> requests) {
        return ResponseEntity.status(HttpStatus.CREATED).body(locationService.createLocations(requests));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResLocationDTO> updateLocation(
            @PathVariable Long id,
            @RequestBody ReqLocationDTO request) {
        return ResponseEntity.ok(locationService.updateLocation(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocation(@PathVariable Long id) {
        locationService.deleteLocation(id);
        return ResponseEntity.noContent().build();
    }
}