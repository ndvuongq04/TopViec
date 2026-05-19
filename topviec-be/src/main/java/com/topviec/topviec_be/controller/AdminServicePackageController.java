package com.topviec.topviec_be.controller;

import com.topviec.topviec_be.annotation.LogAction;
import com.topviec.topviec_be.enums.logging.LogActionType;

import com.topviec.topviec_be.dto.request.ReqServicePackageDTO;
import com.topviec.topviec_be.dto.response.ResServicePackageDTO;
import com.topviec.topviec_be.dto.response.ResultPaginationDTO;
import com.topviec.topviec_be.enums.adminUsers.AdminRoleConstants;
import com.topviec.topviec_be.service.ServicePackageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/service-packages")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminServicePackageController {

    private final ServicePackageService servicePackageService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') and @adminSecurity.hasAnyRole(authentication, '"
            + AdminRoleConstants.SUPER_ADMIN + "', '"
            + AdminRoleConstants.FINANCE_ADMIN + "', '"
            + AdminRoleConstants.SUPPORT_ADMIN + "', '"
            + AdminRoleConstants.CONTENT_MODERATOR + "')")
    public ResponseEntity<ResultPaginationDTO> getAllServicePackages(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 10, sort = "sortOrder") Pageable pageable) {
        return ResponseEntity.ok(servicePackageService.getAllServicePackages(keyword, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') and @adminSecurity.hasAnyRole(authentication, '"
            + AdminRoleConstants.SUPER_ADMIN + "', '"
            + AdminRoleConstants.FINANCE_ADMIN + "')")
    public ResponseEntity<ResServicePackageDTO> getServicePackageById(@PathVariable Long id) {
        return ResponseEntity.ok(servicePackageService.getServicePackageById(id));
    }

    @PostMapping
    @LogAction(LogActionType.CREATE_SERVICE_PACKAGE)
    @PreAuthorize("hasRole('ADMIN') and @adminSecurity.hasAnyRole(authentication, '"
            + AdminRoleConstants.SUPER_ADMIN + "', '"
            + AdminRoleConstants.FINANCE_ADMIN + "')")
    public ResponseEntity<ResServicePackageDTO> createServicePackage(
            @Valid @RequestBody ReqServicePackageDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(servicePackageService.createServicePackage(request));
    }

    @PutMapping("/{id}")
    @LogAction(LogActionType.UPDATE_SERVICE_PACKAGE)
    @PreAuthorize("hasRole('ADMIN') and @adminSecurity.hasAnyRole(authentication, '"
            + AdminRoleConstants.SUPER_ADMIN + "', '"
            + AdminRoleConstants.FINANCE_ADMIN + "')")
    public ResponseEntity<ResServicePackageDTO> updateServicePackage(
            @PathVariable Long id,
            @Valid @RequestBody ReqServicePackageDTO request) {
        return ResponseEntity.ok(servicePackageService.updateServicePackage(id, request));
    }
}
