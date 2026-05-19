package com.topviec.topviec_be.controller;

import com.topviec.topviec_be.dto.response.ResAddonServiceDTO;
import com.topviec.topviec_be.dto.response.ResServicePackageDTO;
import com.topviec.topviec_be.enums.services.ServiceCategory;
import com.topviec.topviec_be.service.AddonServiceService;
import com.topviec.topviec_be.service.ServicePackageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/employer/packages")
@RequiredArgsConstructor
@PreAuthorize("hasRole('EMPLOYER')")
public class EmployerPackageController {

    private final ServicePackageService servicePackageService;
    private final AddonServiceService addonServiceService;

    @GetMapping("/services")
    public ResponseEntity<List<ResServicePackageDTO>> getActiveServicePackages() {
        return ResponseEntity.ok(servicePackageService.getPublicActivePackages());
    }

    @GetMapping("/addons")
    public ResponseEntity<List<ResAddonServiceDTO>> getActiveAddonServices(
            @RequestParam(required = false) ServiceCategory category) {
        return ResponseEntity.ok(addonServiceService.getActiveAddonServices(category));
    }
}
