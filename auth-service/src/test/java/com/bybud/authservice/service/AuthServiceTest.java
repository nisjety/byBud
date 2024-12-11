package com.bybud.authservice.service;

import com.bybud.common.model.RoleName; // Correct import for RoleName
import com.bybud.authservice.dto.*;
import com.bybud.authservice.exception.UserNotFoundException;
import com.bybud.authservice.model.AuthRole;
import com.bybud.authservice.model.AuthUser;
import com.bybud.authservice.repository.AuthRoleRepository;
import com.bybud.authservice.repository.AuthUserRepository;
import com.bybud.common.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private AuthService authService;

    @Mock
    private AuthUserRepository authUserRepository;

    @Mock
    private AuthRoleRepository authRoleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authService = new AuthService(authUserRepository, authRoleRepository, passwordEncoder, authenticationManager, jwtTokenProvider);
    }

    private RegisterRequest createRegisterRequest() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password");
        registerRequest.setRole("ROLE_CUSTOMER");
        return registerRequest;
    }

    private LoginRequest createLoginRequest(String password) {
        return new LoginRequest("testuser", password);
    }

    @Test
    void testRegister_Success() {
        RegisterRequest registerRequest = createRegisterRequest();
        AuthRole role = new AuthRole(RoleName.ROLE_CUSTOMER);
        when(authRoleRepository.findByName(RoleName.ROLE_CUSTOMER)).thenReturn(Optional.of(role));
        when(authUserRepository.existsByUsername("testuser")).thenReturn(false);
        when(authUserRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        AuthUser savedUser = new AuthUser("testuser", "test@example.com", "encodedPassword", Set.of(role));
        when(authUserRepository.save(any(AuthUser.class))).thenReturn(savedUser);

        AuthUser result = authService.register(registerRequest);

        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("encodedPassword", result.getPassword());
        assertTrue(result.getRoles().contains(role));
        verify(authUserRepository).save(any(AuthUser.class));
    }

    @Test
    void testRegister_ExistingUsername() {
        RegisterRequest registerRequest = createRegisterRequest();
        when(authUserRepository.existsByUsername("testuser")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> authService.register(registerRequest),
                "Username is already in use"
        );

        assertEquals("Username is already in use", exception.getMessage());
        verify(authUserRepository, never()).save(any(AuthUser.class));
    }

    @Test
    void testRegister_ExistingEmail() {
        RegisterRequest registerRequest = createRegisterRequest();
        when(authUserRepository.existsByEmail("test@example.com")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> authService.register(registerRequest),
                "Email is already in use"
        );

        assertEquals("Email is already in use", exception.getMessage());
        verify(authUserRepository, never()).save(any(AuthUser.class));
    }

    @Test
    void testLogin_Success() {
        LoginRequest loginRequest = createLoginRequest("password");
        AuthUser authUser = new AuthUser("testuser", "test@example.com", "encodedPassword", Set.of(new AuthRole(RoleName.ROLE_CUSTOMER)));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(Authentication.class));
        when(authUserRepository.findByUsername(eq("testuser"))).thenReturn(Optional.of(authUser));
        when(jwtTokenProvider.generateJwtToken(eq("testuser"))).thenReturn("accessToken");
        when(jwtTokenProvider.generateJwtRefreshToken(eq("testuser"))).thenReturn("refreshToken");

        JwtResponse jwtResponse = authService.login(loginRequest);

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
                () -> authService.login(loginRequest),
                "Bad credentials"
        );

        assertEquals("Bad credentials", exception.getMessage());
        verify(authUserRepository, never()).findByUsername(anyString());
    }

    @Test
    void testRefreshToken_Success() {
        String refreshToken = "validRefreshToken";
        AuthUser authUser = new AuthUser("testuser", "test@example.com", "password", Set.of(new AuthRole(RoleName.ROLE_CUSTOMER)));
        when(jwtTokenProvider.validateJwtRefreshToken(eq(refreshToken))).thenReturn(true);
        when(jwtTokenProvider.getUsernameFromJwtRefreshToken(eq(refreshToken))).thenReturn("testuser");
        when(authUserRepository.findByUsername(eq("testuser"))).thenReturn(Optional.of(authUser));
        when(jwtTokenProvider.generateJwtToken(eq("testuser"))).thenReturn("newAccessToken");

        JwtResponse jwtResponse = authService.refreshToken(refreshToken);

        assertEquals("newAccessToken", jwtResponse.getAccessToken());
        assertEquals(refreshToken, jwtResponse.getRefreshToken());
    }

    @Test
    void testRefreshToken_InvalidToken() {
        String refreshToken = "invalidToken";
        when(jwtTokenProvider.validateJwtRefreshToken(eq(refreshToken))).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> authService.refreshToken(refreshToken),
                "Invalid refresh token"
        );

        assertEquals("Invalid refresh token", exception.getMessage());
        verify(authUserRepository, never()).findByUsername(anyString());
    }

    @Test
    void testGetUserDetails_Success() {
        AuthUser authUser = new AuthUser("testuser", "test@example.com", "encodedPassword", Set.of(new AuthRole(RoleName.ROLE_CUSTOMER)));
        when(authUserRepository.findByUsername(eq("testuser"))).thenReturn(Optional.of(authUser));

        JwtResponse jwtResponse = authService.getUserDetails("testuser");

        assertEquals("testuser", jwtResponse.getUsername());
        assertEquals("test@example.com", jwtResponse.getEmail());
        assertTrue(jwtResponse.getRoles().contains("ROLE_CUSTOMER"));
    }

    @Test
    void testGetUserDetails_UserNotFound() {
        when(authUserRepository.findByUsername(eq("unknownuser"))).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> authService.getUserDetails("unknownuser"),
                "User not found"
        );

        assertEquals("User not found", exception.getMessage());
    }
}
