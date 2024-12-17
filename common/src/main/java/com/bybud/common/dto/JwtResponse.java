package com.bybud.common.dto;

import com.bybud.common.model.RoleName;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class JwtResponse {
    private String accessToken;
    private String refreshToken;
    private String userId;
    private String username;
    private String email;
    private String fullName;
    private Set<RoleName> roles;

    public JwtResponse(String accessToken, String refreshToken, String userId,
                       String username, String email, String fullName, Set<RoleName> roles) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.roles = roles;
    }

    public String getAccessToken() { return accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
    public Set<RoleName> getRoles() { return roles; }
}
