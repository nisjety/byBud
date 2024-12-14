package com.bybud.authservice.service;

import com.bybud.common.dto.JwtResponse;
import com.bybud.common.exception.UserNotFoundException;
import com.bybud.common.model.Role;
import com.bybud.common.model.User;
import com.bybud.common.model.RoleName;
import com.bybud.common.repository.RoleRepository;
import com.bybud.common.repository.UserRepository;
import com.bybud.common.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public User register(String username, String email, String password, String fullName, LocalDate dateOfBirth, String roleName) {
        validateRegistrationData(username, email);

        Role role = roleRepository.findByName(RoleName.valueOf(roleName.toUpperCase()))
                .orElseThrow(() -> new IllegalArgumentException("Invalid role specified"));

        User user = new User(
                username,
                email,
                passwordEncoder.encode(password),
                fullName,
                dateOfBirth,
                Set.of(role)
        );

        return userRepository.save(user);
    }

    public JwtResponse login(String usernameOrEmail, String password) {
        authenticate(usernameOrEmail, password);

        User user = userRepository.findByUsername(usernameOrEmail)
                .or(() -> userRepository.findByEmail(usernameOrEmail))
                .orElseThrow(() -> new IllegalArgumentException("Bad credentials"));

        String accessToken = jwtTokenProvider.generateJwtToken(user.getUsername());
        String refreshToken = jwtTokenProvider.generateJwtRefreshToken(user.getUsername());

        return createJwtResponse(user, accessToken, refreshToken);
    }

    public JwtResponse refreshToken(String refreshToken) {
        if (!jwtTokenProvider.validateJwtRefreshToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        String username = jwtTokenProvider.getUsernameFromJwtRefreshToken(refreshToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        String newAccessToken = jwtTokenProvider.generateJwtToken(username);

        return createJwtResponse(user, newAccessToken, refreshToken);
    }

    public JwtResponse getUserDetails(String usernameOrEmail) {
        User user = userRepository.findByUsername(usernameOrEmail)
                .or(() -> userRepository.findByEmail(usernameOrEmail))
                .orElseThrow(() -> new UserNotFoundException("User not found with username or email: " + usernameOrEmail));

        return createJwtResponse(user, null, null);
    }

    private void authenticate(String usernameOrEmail, String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(usernameOrEmail, password)
        );
    }

    private JwtResponse createJwtResponse(User user, String accessToken, String refreshToken) {
        List<String> roles = user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toList());

        return new JwtResponse(
                accessToken,
                refreshToken,
                String.valueOf(user.getId()),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                roles
        );
    }

    private void validateRegistrationData(String username, String email) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username is already in use");
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email is already in use");
        }
    }
}
