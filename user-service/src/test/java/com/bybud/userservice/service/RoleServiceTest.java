package com.bybud.userservice.service;

import com.bybud.common.dto.RoleDTO;
import com.bybud.common.model.Role;
import com.bybud.common.model.RoleName;
import com.bybud.common.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoleServiceTest {

    private RoleRepository roleRepository;
    private RoleService roleService;

    @BeforeEach
    void setUp() {
        roleRepository = mock(RoleRepository.class);
        roleService = new RoleService(roleRepository);
    }

    @Test
    void testGetAllRoles() {
        // Arrange
        Role role1 = new Role();
        role1.setId(1L);
        role1.setName(RoleName.ROLE_CUSTOMER);

        Role role2 = new Role();
        role2.setId(2L);
        role2.setName(RoleName.ROLE_ADMIN);

        when(roleRepository.findAll()).thenReturn(List.of(role1, role2));

        // Act
        List<RoleDTO> roles = roleService.getAllRoles();

        // Assert
        assertEquals(2, roles.size());
        assertEquals("ROLE_CUSTOMER", roles.get(0).getName());
        assertEquals("ROLE_ADMIN", roles.get(1).getName());

        verify(roleRepository, times(1)).findAll();
    }

    @Test
    void testCreateRoleSuccess() {
        // Arrange
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setName("ROLE_CUSTOMER");

        when(roleRepository.existsByName(RoleName.ROLE_CUSTOMER)).thenReturn(false);

        Role savedRole = new Role();
        savedRole.setId(1L);
        savedRole.setName(RoleName.ROLE_CUSTOMER);

        when(roleRepository.save(any(Role.class))).thenReturn(savedRole);

        // Act
        RoleDTO result = roleService.createRole(roleDTO);

        // Assert
        assertNotNull(result);
        assertEquals("ROLE_CUSTOMER", result.getName());
        verify(roleRepository, times(1)).save(any(Role.class));
    }

    @Test
    void testCreateRoleThrowsExceptionWhenRoleExists() {
        // Arrange
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setName("ROLE_CUSTOMER");
        when(roleRepository.existsByName(RoleName.ROLE_CUSTOMER)).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> roleService.createRole(roleDTO));
        assertEquals("Role already exists", exception.getMessage());

        verify(roleRepository, never()).save(any(Role.class));
    }
}
