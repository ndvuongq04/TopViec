package com.topviec.topviec_be.service.impl;

import com.topviec.topviec_be.dto.response.ResRoleDefaultDTO;
import com.topviec.topviec_be.dto.response.ResRoleSummaryDTO;
import com.topviec.topviec_be.entity.ActionItem;
import com.topviec.topviec_be.entity.RoleDefault;
import com.topviec.topviec_be.enums.companyMember.MemberRole;
import com.topviec.topviec_be.exception.AppException;
import com.topviec.topviec_be.repository.RoleDefaultRepository;
import com.topviec.topviec_be.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleDefaultRepository roleDefaultRepository;

    @Override
    public List<ResRoleDefaultDTO> getDefaultRoles() {
        return roleDefaultRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ResRoleDefaultDTO toggleAction(Long roleId, String actionCode, boolean enabled) {
        RoleDefault role = roleDefaultRepository.findById(roleId)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy role với id: " + roleId));

        List<ActionItem> actions = role.getActions();
        if (actions == null) {
            throw AppException.notFound("Không tìm thấy action '" + actionCode + "' trong role này");
        }

        ActionItem target = actions.stream()
                .filter(a -> a.getCode().equals(actionCode))
                .findFirst()
                .orElseThrow(() -> AppException.notFound("Không tìm thấy action '" + actionCode + "' trong role này"));

        target.setEnabled(enabled);
        return toDTO(roleDefaultRepository.save(role));
    }

    @Override
    @Transactional
    public List<ResRoleDefaultDTO> addActionToAllRoles(String name, String code) {
        List<RoleDefault> allRoles = roleDefaultRepository.findAll();

        boolean alreadyExists = allRoles.stream()
                .anyMatch(r -> r.getActions() != null &&
                        r.getActions().stream().anyMatch(a -> a.getCode().equals(code)));
        if (alreadyExists) {
            throw AppException.conflict("Action với mã '" + code + "' đã tồn tại");
        }

        for (RoleDefault role : allRoles) {
            List<ActionItem> actions = role.getActions() != null ? new ArrayList<>(role.getActions()) : new ArrayList<>();
            boolean enabledForRole = MemberRole.OWNER.equals(role.getRoleName());
            actions.add(ActionItem.builder().name(name).code(code).enabled(enabledForRole).build());
            role.setActions(actions);
        }

        return roleDefaultRepository.saveAll(allRoles).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ResRoleDefaultDTO renameAction(Long roleId, String actionCode, String newName) {
        RoleDefault role = roleDefaultRepository.findById(roleId)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy role với id: " + roleId));

        List<ActionItem> actions = role.getActions();
        ActionItem target = (actions == null ? List.<ActionItem>of() : actions).stream()
                .filter(a -> a.getCode().equals(actionCode))
                .findFirst()
                .orElseThrow(() -> AppException.notFound("Không tìm thấy action '" + actionCode + "' trong role này"));

        target.setName(newName);
        return toDTO(roleDefaultRepository.save(role));
    }

    @Override
    public List<ResRoleSummaryDTO> getRoleSummaries() {
        return roleDefaultRepository.findAll().stream()
                .map(r -> ResRoleSummaryDTO.builder()
                        .id(r.getId())
                        .roleName(r.getRoleName())
                        .build())
                .collect(Collectors.toList());
    }

    private ResRoleDefaultDTO toDTO(RoleDefault role) {
        return ResRoleDefaultDTO.builder()
                .id(role.getId())
                .roleName(role.getRoleName())
                .actions(role.getActions())
                .updatedAt(role.getUpdatedAt())
                .build();
    }
}