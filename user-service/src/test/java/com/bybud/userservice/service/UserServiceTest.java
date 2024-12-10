package com.bybud.userservice.service;

import com.bybud.userservice.dto.CreateUserDTO;
import com.bybud.userservice.dto.UserDTO;
import com.bybud.userservice.model.AppRole;
import com.bybud.userservice.model.AppUser;
import com.bybud.userservice.model.RoleName;
import com.bybud.userservice.repository.AppRoleRepository;
import com.bybud.userservice.repository.AppUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private AppUserRepository appUserRepository;
    private AppRoleRepository appRoleRepository;
    private PasswordEncoder passwordEncoder;
    private UserService userService;

    @BeforeEach
    void setUp() {
        appUserRepository = mock(AppUserRepository.class);
        appRoleRepository = mock(AppRoleRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        userService = new UserService(appUserRepository, appRoleRepository, passwordEncoder);
    }

    @Test
    void testCreateUserSuccess() {
        // Arrange
        CreateUserDTO createUserDTO = new CreateUserDTO();
        createUserDTO.setUsername("testuser");
        createUserDTO.setFullName("Test User");
        createUserDTO.setEmail("test@example.com");
        createUserDTO.setPassword("password123");
        createUserDTO.setDateOfBirth(LocalDate.of(1990, 1, 1));

        AppRole customerRole = new AppRole();
        customerRole.setId(1L);
        customerRole.setName(RoleName.ROLE_CUSTOMER);

        when(appUserRepository.existsByUsername("testuser")).thenReturn(false);
        when(appUserRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(appRoleRepository.findByName(RoleName.ROLE_CUSTOMER)).thenReturn(Optional.of(customerRole));
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        AppUser savedUser = new AppUser();
        savedUser.setId(1L);
        savedUser.setUsername("testuser");
        savedUser.setFullName("Test User");
        savedUser.setEmail("test@example.com");
        savedUser.setPassword("encodedPassword");
        savedUser.setRoles(Set.of(customerRole));
        when(appUserRepository.save(any(AppUser.class))).thenReturn(savedUser);

        // Act
        UserDTO result = userService.createUser(createUserDTO);

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("Test User", result.getFullName());
        assertEquals("test@example.com", result.getEmail());
        assertTrue(result.getRoles().contains("ROLE_CUSTOMER"));

        verify(appUserRepository, times(1)).save(any(AppUser.class));
    }

    @Test
    void testCreateUserThrowsExceptionWhenUsernameExists() {
        // Arrange
        CreateUserDTO createUserDTO = new CreateUserDTO();
        createUserDTO.setUsername("testuser");
        when(appUserRepository.existsByUsername("testuser")).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.createUser(createUserDTO));
        assertEquals("Username is already in use", exception.getMessage());

        verify(appUserRepository, never()).save(any(AppUser.class));
    }

    @Test
    void testCreateUserThrowsExceptionWhenEmailExists() {
        // Arrange
        CreateUserDTO createUserDTO = new CreateUserDTO();
        createUserDTO.setEmail("test@example.com");
        when(appUserRepository.existsByEmail("test@example.com")).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.createUser(createUserDTO));
        assertEquals("Email is already in use", exception.getMessage());

        verify(appUserRepository, never()).save(any(AppUser.class));
    }
}
