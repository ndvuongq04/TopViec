package com.topviec.topviec_be.controller;

import com.topviec.topviec_be.dto.request.ReqAddActionDTO;
import com.topviec.topviec_be.dto.request.ReqRenameActionDTO;
import com.topviec.topviec_be.dto.request.ReqToggleActionDTO;
import com.topviec.topviec_be.dto.response.ResRoleDefaultDTO;
import com.topviec.topviec_be.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/roles")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class RoleController {

    private final RoleService roleService;

    @GetMapping("/default-permissions")
    public ResponseEntity<List<ResRoleDefaultDTO>> getDefaultRoles() {
        return ResponseEntity.ok(roleService.getDefaultRoles());
    }

    @PatchMapping("/{roleId}/actions/{actionName}")
    public ResponseEntity<ResRoleDefaultDTO> toggleAction(
            @PathVariable Long roleId,
            @PathVariable String actionName,
            @Valid @RequestBody ReqToggleActionDTO request) {
        return ResponseEntity.ok(roleService.toggleAction(roleId, actionName, request.getEnabled()));
    }

    @PatchMapping("/{roleId}/actions/{actionCode}/name")
    public ResponseEntity<ResRoleDefaultDTO> renameAction(
            @PathVariable Long roleId,
            @PathVariable String actionCode,
            @Valid @RequestBody ReqRenameActionDTO request) {
        return ResponseEntity.ok(roleService.renameAction(roleId, actionCode, request.getName()));
    }

    @PostMapping("/actions")
    public ResponseEntity<List<ResRoleDefaultDTO>> addAction(
            @Valid @RequestBody ReqAddActionDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(roleService.addActionToAllRoles(request.getName(), request.getCode()));
    }
}