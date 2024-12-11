package com.bybud.userservice.service;

import com.bybud.userservice.dto.RoleDTO;
import com.bybud.userservice.model.AppRole;
import com.bybud.common.model.RoleName;
import com.bybud.userservice.repository.AppRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleService {

    private final AppRoleRepository appRoleRepository;

    @Autowired
    public RoleService(AppRoleRepository appRoleRepository) {
        this.appRoleRepository = appRoleRepository;
    }

    public List<RoleDTO> getAllRoles() {
        return appRoleRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public RoleDTO createRole(RoleDTO roleDTO) {
        if (appRoleRepository.existsByName(RoleName.valueOf(roleDTO.getName()))) {
            throw new IllegalArgumentException("Role already exists");
        }

        AppRole role = new AppRole();
        role.setName(RoleName.valueOf(roleDTO.getName()));
        AppRole savedRole = appRoleRepository.save(role);

        return mapToDTO(savedRole);
    }

    private RoleDTO mapToDTO(AppRole role) {
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setId(role.getId());
        roleDTO.setName(role.getName().name());
        return roleDTO;
    }
}
