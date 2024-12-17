package com.bybud.authservice.controller;

import com.bybud.authservice.dto.LoginRequest;
import com.bybud.authservice.dto.RegisterRequest;
import com.bybud.authservice.service.AuthService;
import com.bybud.common.dto.BaseResponse;
import com.bybud.common.dto.JwtResponse;
import com.bybud.common.dto.UserDTO;
import com.bybud.common.model.RoleName;
import com.bybud.common.security.JwtTokenProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(AuthService authService, JwtTokenProvider jwtTokenProvider) {
        this.authService = authService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/register")
    public ResponseEntity<BaseResponse<?>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        UserDTO userDTO = authService.register(registerRequest);
        return ResponseEntity.ok(BaseResponse.success("User registered successfully.", userDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<?>> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            JwtResponse jwtResponse = authService.login(loginRequest.getUsernameOrEmail(), loginRequest.getPassword());
            return ResponseEntity.ok(BaseResponse.success("Login successful.", jwtResponse));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(BaseResponse.error("Login failed: " + e.getMessage()));
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<BaseResponse<?>> refreshToken(@RequestParam String refreshToken) {
        JwtResponse jwtResponse = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(BaseResponse.success("Token refreshed successfully.", jwtResponse));
    }

    @GetMapping("/user")
    public ResponseEntity<BaseResponse<?>> getUserDetails(@RequestParam String usernameOrEmail) {
        UserDTO userDTO = authService.getUserDetails(usernameOrEmail);
        return ResponseEntity.ok(BaseResponse.success("User details fetched.", userDTO));
    }

    /**
     * Endpoint to validate ADMIN role and ensure secure forwarding of requests.
     */
    @GetMapping("/validate/admin")
    public ResponseEntity<BaseResponse<?>> validateAdmin(HttpServletRequest request) {
        String jwtToken = jwtTokenProvider.extractJwtFromRequest(request);

        // Extract roles from JWT
        String roles = jwtTokenProvider.getRolesFromJwt(jwtToken);

        if (!roles.contains(RoleName.ADMIN.name())) {
            return ResponseEntity.status(403)
                    .body(BaseResponse.error("Access denied. Admin role is required."));
        }

        return ResponseEntity.ok(BaseResponse.success("Admin validation successful.", null));
    }
}
