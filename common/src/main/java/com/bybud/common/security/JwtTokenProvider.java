package com.bybud.common.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@ConditionalOnProperty(name = "bybud.app.jwtSecret")
public class JwtTokenProvider {


    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    private final String jwtSecret;
    private final String jwtRefreshSecret;
    private final int jwtExpirationMs;
    private final int jwtRefreshExpirationMs;

    // Constructor for Dependency Injection
    public JwtTokenProvider(
            @Value("${bybud.app.jwtSecret}") String jwtSecret,
            @Value("${bybud.app.jwtRefreshSecret}") String jwtRefreshSecret,
            @Value("${bybud.app.jwtExpirationMs}") int jwtExpirationMs,
            @Value("${bybud.app.jwtRefreshExpirationMs}") int jwtRefreshExpirationMs) {
        this.jwtSecret = jwtSecret;
        this.jwtRefreshSecret = jwtRefreshSecret;
        this.jwtExpirationMs = jwtExpirationMs;
        this.jwtRefreshExpirationMs = jwtRefreshExpirationMs;

        System.out.println("JwtTokenProvider initialized with secrets");

    }

    // Getter methods for testing (optional)
    public String getJwtSecret() {
        return jwtSecret;
    }

    public String getJwtRefreshSecret() {
        return jwtRefreshSecret;
    }

    public int getJwtExpirationMs() {
        return jwtExpirationMs;
    }

    public int getJwtRefreshExpirationMs() {
        return jwtRefreshExpirationMs;
    }

    // Generate JWT Access Token
    public String generateJwtToken(String username) {
        return Jwts.builder()
                .subject(username) // Sets the subject (user identifier)
                .issuedAt(new Date()) // Sets the current issue time
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs)) // Sets token expiry
                .signWith(getSigningKey(jwtSecret)) // Signs with the provided secret
                .compact();
    }

    // Generate JWT Refresh Token
    public String generateJwtRefreshToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtRefreshExpirationMs))
                .signWith(getSigningKey(jwtRefreshSecret))
                .compact();
    }

    // Extract Username from JWT Token
    public String getUsernameFromJwt(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey(jwtSecret))
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // Extract Username from Refresh Token
    public String getUsernameFromJwtRefreshToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey(jwtRefreshSecret))
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // Validate JWT Token
    public boolean validateJwtToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey(jwtSecret))
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            logger.error("JWT validation error: {}", e.getMessage());
            throw e;
        }
    }

    // Validate JWT Refresh Token
    public boolean validateJwtRefreshToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey(jwtRefreshSecret))
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            logger.error("Refresh Token validation error: {}", e.getMessage());
            throw e;
        }
    }

    // Extract JWT from HTTP Request
    public String extractJwtFromRequest(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }

    // Helper method to retrieve signing key
    private SecretKey getSigningKey(String secret) {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
