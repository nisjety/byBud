package com.bybud.common.security;

import com.bybud.common.model.Role;
import com.bybud.common.model.RoleName;
import com.bybud.common.repository.RoleRepository;
import com.bybud.common.utility.RoleInitializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.mockito.Mockito.*;

class RoleInitializerTest {

    private RoleRepository roleRepository;
    private RoleInitializer roleInitializer;

    @BeforeEach
    void setUp() {
        roleRepository = mock(RoleRepository.class);
        roleInitializer = new RoleInitializer(roleRepository);
    }

    @Test
    void testInitRoles_SavesMissingRoles() {
        // Arrange
        for (RoleName roleName : RoleName.values()) {
            when(roleRepository.existsByName(roleName)).thenReturn(false); // Role doesn't exist
        }

        // Act
        roleInitializer.initRoles();

        // Assert
        for (RoleName roleName : RoleName.values()) {
            verify(roleRepository, times(1)).existsByName(roleName);
            verify(roleRepository, times(1)).save(argThat(role -> role.getName().equals(roleName)));
        }
    }

    @Test
    void testInitRoles_DoesNotSaveExistingRoles() {
        // Arrange
        for (RoleName roleName : RoleName.values()) {
            when(roleRepository.existsByName(roleName)).thenReturn(true); // Role already exists
        }

        // Act
        roleInitializer.initRoles();

        // Assert
        for (RoleName roleName : RoleName.values()) {
            verify(roleRepository, times(1)).existsByName(roleName);
            verify(roleRepository, never()).save(any(Role.class));
        }
    }
}
