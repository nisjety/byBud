package com.bybud.common.dto;

import java.util.List;

public class JwtResponse {

    private final String token;
    private final String refreshToken;
    private final String userId;
    private final String username;
    private final String email;
    private final String fullName;
    private final List<String> roles;

    public JwtResponse(String token, String refreshToken, String userId, String username, String email, String fullName, List<String> roles) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.roles = roles;
    }

    public String getAccessToken() {
        return token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    public List<String> getRoles() {
        return roles;
    }
}
