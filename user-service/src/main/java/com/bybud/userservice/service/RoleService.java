package com.bybud.userservice.service;

import com.bybud.common.dto.RoleDTO;
import com.bybud.common.model.Role;
import com.bybud.common.model.RoleName;
import com.bybud.common.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<RoleDTO> getAllRoles() {
        return roleRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public RoleDTO createRole(RoleDTO roleDTO) {
        if (roleRepository.existsByName(RoleName.valueOf(roleDTO.getName()))) {
            throw new IllegalArgumentException("Role already exists");
        }

        Role role = new Role();
        role.setName(RoleName.valueOf(roleDTO.getName()));
        Role savedRole = roleRepository.save(role);

        return mapToDTO(savedRole);
    }

    private RoleDTO mapToDTO(Role role) {
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setId(role.getId());
        roleDTO.setName(role.getName().name());
        return roleDTO;
    }
}
