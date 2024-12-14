package com.bybud.authservice.controller;

import com.bybud.authservice.service.AuthService;
import com.bybud.common.dto.JwtResponse;
import com.bybud.common.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestParam String username,
                                      @RequestParam String email,
                                      @RequestParam String password,
                                      @RequestParam String fullName,
                                      @RequestParam String dateOfBirth, // Expecting date in ISO format (yyyy-MM-dd)
                                      @RequestParam String role) {
        try {
            LocalDate dob = LocalDate.parse(dateOfBirth); // Parse dateOfBirth
            User newUser = authService.register(username, email, password, fullName, dob, role);
            return ResponseEntity.ok("User registered successfully with ID: " + newUser.getId());
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("Invalid date format for dateOfBirth. Expected format: yyyy-MM-dd.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String usernameOrEmail,
                                   @RequestParam String password) {
        try {
            JwtResponse jwtResponse = authService.login(usernameOrEmail, password);
            return ResponseEntity.ok(jwtResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestParam String refreshToken) {
        try {
            JwtResponse jwtResponse = authService.refreshToken(refreshToken);
            return ResponseEntity.ok(jwtResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserDetails(@RequestParam String usernameOrEmail) {
        try {
            JwtResponse jwtResponse = authService.getUserDetails(usernameOrEmail);
            return ResponseEntity.ok(jwtResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
