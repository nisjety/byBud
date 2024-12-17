package com.bybud.authservice.service;

import com.bybud.authservice.dto.RegisterRequest;
import com.bybud.common.client.UserServiceClient;
import com.bybud.common.dto.CreateUserDTO;
import com.bybud.common.dto.JwtResponse;
import com.bybud.common.dto.UserDTO;
import com.bybud.common.exception.UserNotFoundException;
import com.bybud.common.model.RoleName;
import com.bybud.common.model.User;
import com.bybud.common.repository.UserRepository;
import com.bybud.common.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final UserServiceClient userServiceClient;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtTokenProvider jwtTokenProvider,
                       UserServiceClient userServiceClient) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userServiceClient = userServiceClient;
    }

    public UserDTO register(RegisterRequest registerRequest) {
        String username = registerRequest.getUsername();
        String email = registerRequest.getEmail();
        String password = registerRequest.getPassword();
        String fullName = registerRequest.getFullName();
        LocalDate dateOfBirth = registerRequest.getDateOfBirth();
        RoleName role = registerRequest.getRole();
        String phoneNumber = registerRequest.getPhoneNumber();

        validateRegistration(username, email, phoneNumber);

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setFullName(fullName);
        user.setDateOfBirth(dateOfBirth);
        user.setPhoneNumber(phoneNumber);
        user.setRoles(Set.of(role));
        user.setActive(true);

        userRepository.save(user);

        // Sync user with UserService
        CreateUserDTO createUserDTO = new CreateUserDTO();
        createUserDTO.setUsername(username);
        createUserDTO.setFullName(fullName);
        createUserDTO.setEmail(email);
        createUserDTO.setPassword(password);
        createUserDTO.setDateOfBirth(dateOfBirth);
        createUserDTO.setRoleName(role);
        createUserDTO.setPhoneNumber(phoneNumber);

        userServiceClient.createUser(createUserDTO);

        return mapToUserDTO(user);
    }

    public JwtResponse login(String usernameOrEmail, String password) {
        authenticate(usernameOrEmail, password); // Ensure this method doesn't call login again.
        User user = userRepository.findByUsername(usernameOrEmail)
                .or(() -> userRepository.findByEmail(usernameOrEmail))
                .orElseThrow(() -> new UserNotFoundException("Invalid credentials"));

        return generateJwtResponse(user); // Check if this method avoids recursion.
    }

    public JwtResponse refreshToken(String refreshToken) {
        if (!jwtTokenProvider.validateJwtRefreshToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token.");
        }
        String username = jwtTokenProvider.getUsernameFromJwtRefreshToken(refreshToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return generateJwtResponse(user);
    }

    public UserDTO getUserDetails(String usernameOrEmail) {
        User user = userRepository.findByUsername(usernameOrEmail)
                .or(() -> userRepository.findByEmail(usernameOrEmail))
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return mapToUserDTO(user);
    }

    private void authenticate(String usernameOrEmail, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(usernameOrEmail, password));
    }

    private JwtResponse generateJwtResponse(User user) {
        String accessToken = jwtTokenProvider.generateJwtToken(user.getUsername());
        String refreshToken = jwtTokenProvider.generateJwtRefreshToken(user.getUsername());

        return new JwtResponse(
                accessToken,
                refreshToken,
                user.getId().toString(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getRoles()
        );
    }

    private UserDTO mapToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setFullName(user.getFullName());
        userDTO.setEmail(user.getEmail());
        userDTO.setPhoneNumber(user.getPhoneNumber());
        userDTO.setActive(user.isActive());
        userDTO.setDateOfBirth(user.getDateOfBirth());
        userDTO.setRoles(user.getRoles());
        return userDTO;
    }

    private void validateRegistration(String username, String email, String phoneNumber) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username is already taken.");
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email is already taken.");
        }
        if (!phoneNumber.matches("^[0-9]{7,15}$")) {
            throw new IllegalArgumentException("Phone number must be between 7 and 15 digits.");
        }
    }
}
