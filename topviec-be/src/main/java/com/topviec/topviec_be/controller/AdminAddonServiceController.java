package com.topviec.topviec_be.controller;

import com.topviec.topviec_be.annotation.LogAction;
import com.topviec.topviec_be.enums.logging.LogActionType;

import com.topviec.topviec_be.dto.request.ReqAddonServiceDTO;
import com.topviec.topviec_be.dto.response.ResAddonServiceDTO;
import com.topviec.topviec_be.dto.response.ResultPaginationDTO;
import com.topviec.topviec_be.enums.adminUsers.AdminRoleConstants;
import com.topviec.topviec_be.enums.services.ServiceCategory;
import com.topviec.topviec_be.service.AddonServiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/addon-services")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminAddonServiceController {

    private final AddonServiceService addonServiceService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') and @adminSecurity.hasAnyRole(authentication, '"
            + AdminRoleConstants.SUPER_ADMIN + "', '"
            + AdminRoleConstants.FINANCE_ADMIN + "', '"
            + AdminRoleConstants.SUPPORT_ADMIN + "', '"
            + AdminRoleConstants.CONTENT_MODERATOR + "')")
    public ResponseEntity<ResultPaginationDTO> getAllAddonServices(
            @RequestParam(required = false) ServiceCategory category,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(addonServiceService.getAllAddonServices(category, keyword, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') and @adminSecurity.hasAnyRole(authentication, '"
            + AdminRoleConstants.SUPER_ADMIN + "', '"
            + AdminRoleConstants.FINANCE_ADMIN + "')")
    public ResponseEntity<ResAddonServiceDTO> getAddonServiceById(@PathVariable Long id) {
        return ResponseEntity.ok(addonServiceService.getAddonServiceById(id));
    }

    @PostMapping
    @LogAction(LogActionType.CREATE_ADDON_SERVICE)
    @PreAuthorize("hasRole('ADMIN') and @adminSecurity.hasAnyRole(authentication, '"
            + AdminRoleConstants.SUPER_ADMIN + "', '"
            + AdminRoleConstants.FINANCE_ADMIN + "')")
    public ResponseEntity<ResAddonServiceDTO> createAddonService(
            @Valid @RequestBody ReqAddonServiceDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(addonServiceService.createAddonService(request));
    }

    @PutMapping("/{id}")
    @LogAction(LogActionType.UPDATE_ADDON_SERVICE)
    @PreAuthorize("hasRole('ADMIN') and @adminSecurity.hasAnyRole(authentication, '"
            + AdminRoleConstants.SUPER_ADMIN + "', '"
            + AdminRoleConstants.FINANCE_ADMIN + "')")
    public ResponseEntity<ResAddonServiceDTO> updateAddonService(
            @PathVariable Long id,
            @Valid @RequestBody ReqAddonServiceDTO request) {
        return ResponseEntity.ok(addonServiceService.updateAddonService(id, request));
    }
}
