package com.bybud.authservice.service;

import com.bybud.authservice.dto.RegisterRequest;
import com.bybud.common.client.UserServiceClient;
import com.bybud.common.dto.UserDTO;
import com.bybud.common.exception.UserNotFoundException;
import com.bybud.common.model.RoleName;
import com.bybud.common.model.User;
import com.bybud.common.repository.UserRepository;
import com.bybud.common.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private UserServiceClient userServiceClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authService = new AuthService(userRepository, passwordEncoder, authenticationManager, jwtTokenProvider, userServiceClient);
    }

    private RegisterRequest createRegisterRequest() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setFullName("Test User");
        request.setDateOfBirth(LocalDate.of(1990, 1, 1));
        request.setRole(RoleName.CUSTOMER);
        request.setPhoneNumber("1234567890");
        return request;
    }

    @Test
    void testRegister_ExistingUsername() {
        // Arrange
        RegisterRequest registerRequest = createRegisterRequest();
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                authService.register(registerRequest)
        );

        assertEquals("Username is already taken.", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegister_ExistingEmail() {
        // Arrange
        RegisterRequest registerRequest = createRegisterRequest();
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                authService.register(registerRequest)
        );

        assertEquals("Email is already taken.", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegister_Success() {
        // Arrange
        RegisterRequest registerRequest = createRegisterRequest();
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        User savedUser = new User();
        savedUser.setUsername("testuser");
        savedUser.setEmail("test@example.com");
        savedUser.setFullName("Test User");
        savedUser.setRoles(Set.of(RoleName.CUSTOMER));
        savedUser.setActive(true);
        savedUser.setDateOfBirth(LocalDate.of(1990,1,1));
        savedUser.setPhoneNumber("1234567890");

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Mocking userServiceClient.createUser to return a UserDTO instead of doNothing()
        when(userServiceClient.createUser(any())).thenReturn(new UserDTO());

        // Act
        UserDTO result = authService.register(registerRequest);

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertTrue(result.getRoles().contains(RoleName.CUSTOMER));
        verify(userRepository, times(1)).save(any(User.class));
        verify(userServiceClient, times(1)).createUser(any());
    }

    @Test
    void testLogin_InvalidCredentials() {
        // Arrange
        // Simulate successful authentication but no user found to throw UserNotFoundException
        Authentication mockAuth = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(mockAuth);
        when(userRepository.findByUsername("invalid")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("invalid")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> authService.login("invalid", "password"));
    }
}
