package com.bybud.userservice.controller;

import com.bybud.common.dto.BaseResponse;
import com.bybud.common.dto.CreateUserDTO;
import com.bybud.common.dto.UserDTO;
import com.bybud.userservice.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<BaseResponse<UserDTO>> createUser(@Valid @RequestBody CreateUserDTO createUserDTO) {
        logger.info("Received request to create user with username: {}", createUserDTO.getUsername());
        UserDTO userDTO = userService.createUser(createUserDTO);
        return ResponseEntity.ok(BaseResponse.success("User created successfully.", userDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<UserDTO>> getUserById(@PathVariable("id") Long id) {
        logger.info("Received request to get user by ID: {}", id);
        UserDTO userDTO = userService.getUserById(id);
        return ResponseEntity.ok(BaseResponse.success("User fetched successfully.", userDTO));
    }

    @GetMapping("/details")
    public ResponseEntity<BaseResponse<UserDTO>> getUserDetails(@RequestParam("usernameOrEmail") String usernameOrEmail) {
        logger.info("Received request to get user details for: {}", usernameOrEmail);
        UserDTO userDTO = userService.getUserDetails(usernameOrEmail);
        return ResponseEntity.ok(BaseResponse.success("User details fetched successfully.", userDTO));
    }

    @GetMapping("/admin/all")
    public ResponseEntity<BaseResponse<List<UserDTO>>> getAllUsers() {
        logger.info("Admin request to fetch all users.");
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(BaseResponse.success("All users fetched successfully.", users));
    }
}
