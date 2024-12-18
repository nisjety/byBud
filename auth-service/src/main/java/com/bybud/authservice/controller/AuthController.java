package com.bybud.authservice.controller;

import com.bybud.common.dto.LoginRequest;
import com.bybud.common.dto.RegisterRequest;
import com.bybud.authservice.service.AuthService;
import com.bybud.common.dto.BaseResponse;
import com.bybud.common.dto.UserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<BaseResponse<UserDTO>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            UserDTO userDTO = authService.register(registerRequest);
            return ResponseEntity.ok(BaseResponse.success("User registered successfully.", userDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(BaseResponse.error("Registration failed: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<UserDTO>> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            UserDTO userDTO = authService.login(loginRequest.getUsernameOrEmail(), loginRequest.getPassword());
            return ResponseEntity.ok(BaseResponse.success("Login successful.", userDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(BaseResponse.error("Login failed: " + e.getMessage()));
        }
    }

    @GetMapping("/user")
    public ResponseEntity<BaseResponse<UserDTO>> getUserDetails(@RequestParam String usernameOrEmail) {
        try {
            UserDTO userDTO = authService.getUserDetails(usernameOrEmail);
            return ResponseEntity.ok(BaseResponse.success("User details fetched successfully.", userDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(BaseResponse.error("Failed to fetch user details: " + e.getMessage()));
        }
    }
}
