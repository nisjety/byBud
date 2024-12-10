package com.bybud.userservice.service;

import com.bybud.userservice.dto.CreateUserDTO;
import com.bybud.userservice.dto.UserDTO;
import com.bybud.userservice.model.AppRole;
import com.bybud.userservice.model.AppUser;
import com.bybud.userservice.model.RoleName;
import com.bybud.userservice.repository.AppRoleRepository;
import com.bybud.userservice.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final AppUserRepository appUserRepository;
    private final AppRoleRepository appRoleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(AppUserRepository appUserRepository, AppRoleRepository appRoleRepository, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.appRoleRepository = appRoleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDTO createUser(CreateUserDTO createUserDTO) {
        if (appUserRepository.existsByUsername(createUserDTO.getUsername())) {
            throw new IllegalArgumentException("Username is already in use");
        }

        if (appUserRepository.existsByEmail(createUserDTO.getEmail())) {
            throw new IllegalArgumentException("Email is already in use");
        }

        AppUser user = new AppUser();
        user.setUsername(createUserDTO.getUsername());
        user.setFullName(createUserDTO.getFullName());
        user.setEmail(createUserDTO.getEmail());
        user.setPassword(passwordEncoder.encode(createUserDTO.getPassword()));
        user.setDateOfBirth(createUserDTO.getDateOfBirth());
        user.setActive(true);

        // Assign default role
        AppRole defaultRole = appRoleRepository.findByName(RoleName.ROLE_CUSTOMER)
                .orElseThrow(() -> new IllegalArgumentException("Default role not found"));
        user.getRoles().add(defaultRole);

        AppUser savedUser = appUserRepository.save(user);

        return mapToDTO(savedUser);
    }

    public List<UserDTO> getAllUsers() {
        return appUserRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public UserDTO getUserById(Long id) {
        return appUserRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private UserDTO mapToDTO(AppUser user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setFullName(user.getFullName());
        userDTO.setEmail(user.getEmail());
        userDTO.setActive(user.isActive());
        userDTO.setDateOfBirth(user.getDateOfBirth());
        userDTO.setRoles(user.getRoles().stream().map(role -> role.getName().name()).collect(Collectors.toSet()));
        return userDTO;
    }
}
