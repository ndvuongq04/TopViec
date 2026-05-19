package com.topviec.topviec_be.controller;

import com.topviec.topviec_be.dto.response.ResRoleDefaultDTO;
import com.topviec.topviec_be.dto.response.ResRoleSummaryDTO;
import com.topviec.topviec_be.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/employer/roles")
@RequiredArgsConstructor
@PreAuthorize("hasRole('EMPLOYER')")
public class EmployerRoleController {

    private final RoleService roleService;

    @GetMapping
    public ResponseEntity<List<ResRoleSummaryDTO>> getRoleSummaries() {
        return ResponseEntity.ok(roleService.getRoleSummaries());
    }

    @GetMapping("/default-permissions")
    public ResponseEntity<List<ResRoleDefaultDTO>> getDefaultRoles() {
        return ResponseEntity.ok(roleService.getDefaultRoles());
    }
}
