package com.bybud.authservice.service;

import com.bybud.authservice.dto.LoginRequest;
import com.bybud.authservice.dto.RegisterRequest;
import com.bybud.common.dto.JwtResponse;
import com.bybud.common.exception.UserNotFoundException;
import com.bybud.common.model.Role;
import com.bybud.common.model.RoleName;
import com.bybud.common.model.User;
import com.bybud.common.repository.RoleRepository;
import com.bybud.common.repository.UserRepository;
import com.bybud.common.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authService = new AuthService(userRepository, roleRepository, passwordEncoder, authenticationManager, jwtTokenProvider);
    }

    private RegisterRequest createRegisterRequest() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password");
        registerRequest.setFullName("Test User");
        registerRequest.setDateOfBirth(LocalDate.of(1990, 1, 1));
        registerRequest.setRole("ROLE_CUSTOMER");
        return registerRequest;
    }

    private LoginRequest createLoginRequest(String password) {
        return new LoginRequest("testuser", password);
    }

    @Test
    void testRegister_Success() {
        RegisterRequest registerRequest = createRegisterRequest();
        Role role = new Role(RoleName.ROLE_CUSTOMER);

        when(roleRepository.findByName(RoleName.ROLE_CUSTOMER)).thenReturn(Optional.of(role));
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        User user = new User("testuser", "test@example.com", "encodedPassword", "Test User", LocalDate.of(1990, 1, 1), Set.of(role));

        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = authService.register(
                registerRequest.getUsername(),
                registerRequest.getEmail(),
                registerRequest.getPassword(),
                registerRequest.getFullName(),
                registerRequest.getDateOfBirth(),
                registerRequest.getRole()
        );

        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("encodedPassword", result.getPassword());
        assertEquals("Test User", result.getFullName());
        assertEquals(LocalDate.of(1990, 1, 1), result.getDateOfBirth());
        assertTrue(result.getRoles().contains(role));
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testRegister_ExistingUsername() {
        RegisterRequest registerRequest = createRegisterRequest();
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> authService.register(
                        registerRequest.getUsername(),
                        registerRequest.getEmail(),
                        registerRequest.getPassword(),
                        registerRequest.getFullName(),
                        registerRequest.getDateOfBirth(),
                        registerRequest.getRole()));

        assertEquals("Username is already in use", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegister_ExistingEmail() {
        RegisterRequest registerRequest = createRegisterRequest();
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> authService.register(
                        registerRequest.getUsername(),
                        registerRequest.getEmail(),
                        registerRequest.getPassword(),
                        registerRequest.getFullName(),
                        registerRequest.getDateOfBirth(),
                        registerRequest.getRole()));

        assertEquals("Email is already in use", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testLogin_Success() {
        LoginRequest loginRequest = createLoginRequest("password");
        Role role = new Role(RoleName.ROLE_CUSTOMER);

        User user = new User("testuser", "test@example.com", "encodedPassword", "Test User", LocalDate.of(1990, 1, 1), Set.of(role));

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(Authentication.class));
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(jwtTokenProvider.generateJwtToken("testuser")).thenReturn("accessToken");
        when(jwtTokenProvider.generateJwtRefreshToken("testuser")).thenReturn("refreshToken");

        JwtResponse jwtResponse = authService.login(loginRequest.getUsernameOrEmail(), loginRequest.getPassword());

        assertEquals("accessToken", jwtResponse.getAccessToken());
        assertEquals("refreshToken", jwtResponse.getRefreshToken());
        assertNotNull(jwtResponse.getRoles());
        assertTrue(jwtResponse.getRoles().contains("ROLE_CUSTOMER"));
    }

    @Test
    void testLogin_InvalidCredentials() {
        LoginRequest loginRequest = createLoginRequest("wrongpassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new IllegalArgumentException("Bad credentials"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> authService.login(loginRequest.getUsernameOrEmail(), loginRequest.getPassword()));

        assertEquals("Bad credentials", exception.getMessage());
        verify(userRepository, never()).findByUsername(anyString());
    }

    @Test
    void testRefreshToken_Success() {
        String refreshToken = "validRefreshToken";
        Role role = new Role(RoleName.ROLE_CUSTOMER);

        User user = new User("testuser", "test@example.com", "password", "Test User", LocalDate.of(1990, 1, 1), Set.of(role));

        when(jwtTokenProvider.validateJwtRefreshToken(refreshToken)).thenReturn(true);
        when(jwtTokenProvider.getUsernameFromJwtRefreshToken(refreshToken)).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(jwtTokenProvider.generateJwtToken("testuser")).thenReturn("newAccessToken");

        JwtResponse jwtResponse = authService.refreshToken(refreshToken);

        assertEquals("newAccessToken", jwtResponse.getAccessToken());
        assertEquals(refreshToken, jwtResponse.getRefreshToken());
    }

    @Test
    void testRefreshToken_InvalidToken() {
        String refreshToken = "invalidToken";
        when(jwtTokenProvider.validateJwtRefreshToken(refreshToken)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> authService.refreshToken(refreshToken));

        assertEquals("Invalid refresh token", exception.getMessage());
        verify(userRepository, never()).findByUsername(anyString());
    }

    @Test
    void testGetUserDetails_Success() {
        Role role = new Role(RoleName.ROLE_CUSTOMER);

        User user = new User("testuser", "test@example.com", "password", "Test User", LocalDate.of(1990, 1, 1), Set.of(role));

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        JwtResponse jwtResponse = authService.getUserDetails("testuser");

        assertEquals("testuser", jwtResponse.getUsername());
        assertEquals("test@example.com", jwtResponse.getEmail());
        assertTrue(jwtResponse.getRoles().contains("ROLE_CUSTOMER"));
    }

    @Test
    void testGetUserDetails_UserNotFound() {
        when(userRepository.findByUsername("unknownuser")).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> authService.getUserDetails("unknownuser"));

        assertEquals("User not found with username or email: unknownuser", exception.getMessage());
    }
}
