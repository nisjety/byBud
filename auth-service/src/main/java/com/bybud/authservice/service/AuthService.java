package com.bybud.authservice.service;

import com.bybud.authservice.dto.*;
import com.bybud.authservice.exception.UserNotFoundException;
import com.bybud.authservice.model.AuthRole;
import com.bybud.authservice.model.AuthUser;
import com.bybud.authservice.repository.AuthRoleRepository;
import com.bybud.authservice.repository.AuthUserRepository;
import com.bybud.authservice.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final AuthUserRepository authUserRepository;
    private final AuthRoleRepository authRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(AuthUserRepository authUserRepository,
                       AuthRoleRepository authRoleRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtTokenProvider jwtTokenProvider) {
        this.authUserRepository = authUserRepository;
        this.authRoleRepository = authRoleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public AuthUser register(RegisterRequest registerRequest) {
        validateRegistrationData(registerRequest);

        AuthRole role = authRoleRepository.findByName(registerRequest.getRole().toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("Invalid role specified"));

        AuthUser user = new AuthUser();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setActive(true);
        user.setRoles(Set.of(role));

        return authUserRepository.save(user);
    }

    public JwtResponse login(LoginRequest loginRequest) {
        AuthUser user = authenticate(loginRequest);

        String accessToken = jwtTokenProvider.generateJwtToken(user.getUsername());
        String refreshToken = jwtTokenProvider.generateJwtRefreshToken(user.getUsername());

        return createJwtResponse(user, accessToken, refreshToken);
    }

    public JwtResponse refreshToken(String refreshToken) {
        if (!jwtTokenProvider.validateJwtRefreshToken(refreshToken)) {
            logger.error("Invalid refresh token: {}", refreshToken);
            throw new IllegalArgumentException("Invalid refresh token");
        }

        String username = jwtTokenProvider.getUsernameFromJwtRefreshToken(refreshToken);
        AuthUser user = authUserRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        String newAccessToken = jwtTokenProvider.generateJwtToken(username);

        return createJwtResponse(user, newAccessToken, refreshToken);
    }

    public JwtResponse getUserDetails(String usernameOrEmail) {
        AuthUser user = authUserRepository.findByUsername(usernameOrEmail)
                .or(() -> authUserRepository.findByEmail(usernameOrEmail))
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return createJwtResponse(user, null, null);
    }

    private AuthUser authenticate(LoginRequest loginRequest) {
        String identifier = loginRequest.getUsernameOrEmail();

        // Perform authentication before fetching user details
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(identifier, loginRequest.getPassword())
        );

        // If authentication succeeds, fetch the user
        return authUserRepository.findByUsername(identifier)
                .or(() -> authUserRepository.findByEmail(identifier))
                .orElseThrow(() -> new IllegalArgumentException("Bad credentials"));
    }


    private JwtResponse createJwtResponse(AuthUser user, String accessToken, String refreshToken) {
        List<String> roles = user.getRoles().stream()
                .map(AuthRole::getName)
                .collect(Collectors.toList());

        return new JwtResponse(
                accessToken,
                refreshToken,
                String.valueOf(user.getId()),
                user.getUsername(),
                user.getEmail(),
                user.getUsername(),
                roles
        );
    }

    private void validateRegistrationData(RegisterRequest registerRequest) {
        if (authUserRepository.existsByUsername(registerRequest.getUsername())) {
            throw new IllegalArgumentException("Username is already in use");
        }
        if (authUserRepository.existsByEmail(registerRequest.getEmail())) {
            throw new IllegalArgumentException("Email is already in use");
        }
    }
}
