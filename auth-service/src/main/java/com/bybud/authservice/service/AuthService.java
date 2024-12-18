package com.bybud.authservice.service;

import com.bybud.common.dto.RegisterRequest;
import com.bybud.common.client.UserServiceClient;
import com.bybud.common.dto.CreateUserDTO;
import com.bybud.common.dto.UserDTO;
import com.bybud.common.exception.UserNotFoundException;
import com.bybud.common.model.RoleName;
import com.bybud.common.model.User;
import com.bybud.common.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Set;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final UserServiceClient userServiceClient;

    public AuthService(UserRepository userRepository, UserServiceClient userServiceClient) {
        this.userRepository = userRepository;
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
        user.setPassword(password);
        user.setFullName(fullName);
        user.setDateOfBirth(dateOfBirth);
        user.setPhoneNumber(phoneNumber);
        user.setRoles(Set.of(role));
        user.setActive(true);

        userRepository.save(user);

        // Sync with UserService
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

    public UserDTO login(String usernameOrEmail, String password) {
        User user = userRepository.findByUsername(usernameOrEmail)
                .or(() -> userRepository.findByEmail(usernameOrEmail))
                .orElseThrow(() -> new UserNotFoundException("Invalid credentials"));

        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("Invalid password.");
        }

        return mapToUserDTO(user);
    }

    public UserDTO getUserDetails(String usernameOrEmail) {
        User user = userRepository.findByUsername(usernameOrEmail)
                .or(() -> userRepository.findByEmail(usernameOrEmail))
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return mapToUserDTO(user);
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
    }
}
