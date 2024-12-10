package com.bybud.userservice.service;

import com.bybud.userservice.dto.RoleDTO;
import com.bybud.userservice.model.AppRole;
import com.bybud.userservice.model.RoleName;
import com.bybud.userservice.repository.AppRoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoleServiceTest {

    private AppRoleRepository appRoleRepository;
    private RoleService roleService;

    @BeforeEach
    void setUp() {
        appRoleRepository = mock(AppRoleRepository.class);
        roleService = new RoleService(appRoleRepository);
    }

    @Test
    void testGetAllRoles() {
        // Arrange
        AppRole role1 = new AppRole();
        role1.setId(1L);
        role1.setName(RoleName.ROLE_CUSTOMER);

        AppRole role2 = new AppRole();
        role2.setId(2L);
        role2.setName(RoleName.ROLE_ADMIN);

        when(appRoleRepository.findAll()).thenReturn(List.of(role1, role2));

        // Act
        List<RoleDTO> roles = roleService.getAllRoles();

        // Assert
        assertEquals(2, roles.size());
        assertEquals("ROLE_CUSTOMER", roles.get(0).getName());
        assertEquals("ROLE_ADMIN", roles.get(1).getName());

        verify(appRoleRepository, times(1)).findAll();
    }

    @Test
    void testCreateRoleSuccess() {
        // Arrange
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setName("ROLE_CUSTOMER");

        when(appRoleRepository.existsByName(RoleName.ROLE_CUSTOMER)).thenReturn(false);

        AppRole savedRole = new AppRole();
        savedRole.setId(1L);
        savedRole.setName(RoleName.ROLE_CUSTOMER);

        when(appRoleRepository.save(any(AppRole.class))).thenReturn(savedRole);

        // Act
        RoleDTO result = roleService.createRole(roleDTO);

        // Assert
        assertNotNull(result);
        assertEquals("ROLE_CUSTOMER", result.getName());
        verify(appRoleRepository, times(1)).save(any(AppRole.class));
    }

    @Test
    void testCreateRoleThrowsExceptionWhenRoleExists() {
        // Arrange
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setName("ROLE_CUSTOMER");
        when(appRoleRepository.existsByName(RoleName.ROLE_CUSTOMER)).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> roleService.createRole(roleDTO));
        assertEquals("Role already exists", exception.getMessage());

        verify(appRoleRepository, never()).save(any(AppRole.class));
    }
}
