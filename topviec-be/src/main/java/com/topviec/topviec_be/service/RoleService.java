package com.topviec.topviec_be.service;

import com.topviec.topviec_be.dto.response.ResRoleDefaultDTO;
import com.topviec.topviec_be.dto.response.ResRoleSummaryDTO;

import java.util.List;

public interface RoleService {
    List<ResRoleDefaultDTO> getDefaultRoles();
    List<ResRoleSummaryDTO> getRoleSummaries();
    ResRoleDefaultDTO toggleAction(Long roleId, String actionName, boolean enabled);
    List<ResRoleDefaultDTO> addActionToAllRoles(String name, String code);
    ResRoleDefaultDTO renameAction(Long roleId, String actionCode, String newName);
}