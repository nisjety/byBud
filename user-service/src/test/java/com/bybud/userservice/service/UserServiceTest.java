package com.bybud.userservice.service;

import com.bybud.common.dto.CreateUserDTO;
import com.bybud.common.dto.UserDTO;
import com.bybud.common.exception.UserNotFoundException;
import com.bybud.common.model.RoleName;
import com.bybud.common.model.User;
import com.bybud.common.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        userService = new UserService(userRepository, passwordEncoder);
    }

    @Test
    void testCreateUser_Success() {
        // Arrange
        CreateUserDTO createUserDTO = new CreateUserDTO();
        createUserDTO.setUsername("testuser");
        createUserDTO.setFullName("Test User");
        createUserDTO.setEmail("test@example.com");
        createUserDTO.setPassword("password123");
        createUserDTO.setDateOfBirth(LocalDate.of(1990, 1, 1));
        createUserDTO.setRoleName(RoleName.COURIER);
        createUserDTO.setPhoneNumber("1234567890");

        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        User savedUser = new User();
        savedUser.setUsername("testuser");
        savedUser.setFullName("Test User");
        savedUser.setEmail("test@example.com");
        savedUser.setPassword("encodedPassword");
        savedUser.setDateOfBirth(LocalDate.of(1990, 1, 1));
        savedUser.setRoles(Set.of(RoleName.COURIER));
        savedUser.setActive(true);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        UserDTO result = userService.createUser(createUserDTO);

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("Test User", result.getFullName());
        assertEquals("test@example.com", result.getEmail());
        assertEquals(LocalDate.of(1990, 1, 1), result.getDateOfBirth());
        assertTrue(result.getRoles().contains(RoleName.COURIER));

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testCreateUser_UsernameExists() {
        // Arrange
        CreateUserDTO createUserDTO = new CreateUserDTO();
        createUserDTO.setUsername("testuser");
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(createUserDTO)
        );
        assertEquals("Username is already in use.", exception.getMessage());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testCreateUser_EmailExists() {
        // Arrange
        CreateUserDTO createUserDTO = new CreateUserDTO();
        createUserDTO.setEmail("test@example.com");
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(createUserDTO)
        );
        assertEquals("Email is already in use.", exception.getMessage());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testGetAllUsers() {
        // Arrange
        User user1 = new User();
        user1.setUsername("testuser1");
        user1.setEmail("test1@example.com");

        User user2 = new User();
        user2.setUsername("testuser2");
        user2.setEmail("test2@example.com");

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        // Act
        var result = userService.getAllUsers();

        // Assert
        assertEquals(2, result.size());
        assertEquals("testuser1", result.get(0).getUsername());
        assertEquals("testuser2", result.get(1).getUsername());

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetUserById_NotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(99L));
    }

    @Test
    void testGetUserDetails_Username() {
        User user = new User();
        user.setUsername("johndoe");
        user.setEmail("johndoe@example.com");
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(user));

        UserDTO dto = userService.getUserDetails("johndoe");
        assertEquals("johndoe", dto.getUsername());
    }

    @Test
    void testGetUserDetails_Email() {
        User user = new User();
        user.setUsername("janedoe");
        user.setEmail("jane@example.com");
        when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(user));

        UserDTO dto = userService.getUserDetails("jane@example.com");
        assertEquals("janedoe", dto.getUsername());
    }
}

