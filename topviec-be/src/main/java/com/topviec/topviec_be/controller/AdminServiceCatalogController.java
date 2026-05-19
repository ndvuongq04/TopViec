package com.topviec.topviec_be.controller;

import com.topviec.topviec_be.annotation.LogAction;
import com.topviec.topviec_be.enums.logging.LogActionType;

import com.topviec.topviec_be.dto.request.ReqServiceDTO;
import com.topviec.topviec_be.dto.response.ResAdminServiceStatisticsDTO;
import com.topviec.topviec_be.dto.response.ResServiceDTO;
import com.topviec.topviec_be.dto.response.ResultPaginationDTO;
import com.topviec.topviec_be.enums.adminUsers.AdminRoleConstants;
import com.topviec.topviec_be.enums.services.ServiceCategory;
import com.topviec.topviec_be.service.ServiceCatalogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/services")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminServiceCatalogController {

    private final ServiceCatalogService serviceCatalogService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') and @adminSecurity.hasAnyRole(authentication, '"
            + AdminRoleConstants.SUPER_ADMIN + "', '"
            + AdminRoleConstants.FINANCE_ADMIN + "', '"
            + AdminRoleConstants.SUPPORT_ADMIN + "', '"
            + AdminRoleConstants.CONTENT_MODERATOR + "')")
    public ResponseEntity<ResultPaginationDTO> getAllServices(
            @RequestParam(required = false) ServiceCategory category,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(serviceCatalogService.getAllServices(category, keyword, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') and @adminSecurity.hasAnyRole(authentication, '"
            + AdminRoleConstants.SUPER_ADMIN + "', '"
            + AdminRoleConstants.FINANCE_ADMIN + "')")
    public ResponseEntity<ResServiceDTO> getServiceById(@PathVariable Long id) {
        return ResponseEntity.ok(serviceCatalogService.getServiceById(id));
    }

    @PostMapping
    @LogAction(LogActionType.CREATE_SERVICE)
    @PreAuthorize("hasRole('ADMIN') and @adminSecurity.hasAnyRole(authentication, '"
            + AdminRoleConstants.SUPER_ADMIN + "', '"
            + AdminRoleConstants.FINANCE_ADMIN + "')")
    public ResponseEntity<ResServiceDTO> createService(@Valid @RequestBody ReqServiceDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(serviceCatalogService.createService(request));
    }

    @PutMapping("/{id}")
    @LogAction(LogActionType.UPDATE_SERVICE)
    @PreAuthorize("hasRole('ADMIN') and @adminSecurity.hasAnyRole(authentication, '"
            + AdminRoleConstants.SUPER_ADMIN + "', '"
            + AdminRoleConstants.FINANCE_ADMIN + "')")
    public ResponseEntity<ResServiceDTO> updateService(
            @PathVariable Long id,
            @Valid @RequestBody ReqServiceDTO request) {
        return ResponseEntity.ok(serviceCatalogService.updateService(id, request));
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN') and @adminSecurity.hasAnyRole(authentication, '"
            + AdminRoleConstants.SUPER_ADMIN + "', '"
            + AdminRoleConstants.FINANCE_ADMIN + "')")
    public ResponseEntity<ResAdminServiceStatisticsDTO> getServiceStatistics() {
        return ResponseEntity.ok(serviceCatalogService.getServiceStatistics());
    }
}
