package com.bybud.common.security;

import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

public class JwtTokenProviderTest {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProviderTest.class);

    private final JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(
            "VGhpcyBpcyBhIHZlcnkgc2VjdXJlIGtleSBmb3IgdGVzdGluZy4=", // JWT Secret
            "VGhpcyBpcyBhIHZlcnkgc2VjdXJlIGtleSBmb3IgdGVzdGluZy4=", // Refresh Secret
            3600000, // 1 hour expiration
            86400000 // 1 day refresh expiration
    );

    @Test
    void testInitialization() {
        logger.info("Testing JwtTokenProvider initialization...");
        assertNotNull(jwtTokenProvider.getJwtSecret(), "JWT secret should not be null");
        assertNotNull(jwtTokenProvider.getJwtRefreshSecret(), "JWT refresh secret should not be null");
        assertTrue(jwtTokenProvider.getJwtExpirationMs() > 0, "JWT expiration time should be greater than 0");
        assertTrue(jwtTokenProvider.getJwtRefreshExpirationMs() > 0, "JWT refresh expiration time should be greater than 0");
        logger.info("JwtTokenProvider initialized with jwtSecret: {}, jwtRefreshSecret: {}",
                jwtTokenProvider.getJwtSecret(), jwtTokenProvider.getJwtRefreshSecret());
    }

    @Test
    void testGenerateAndValidateJwtToken() {
        String token = jwtTokenProvider.generateJwtToken("testUser");

        logger.info("Generated JWT token: {}", token);
        assertNotNull(token, "Token should not be null");
        assertTrue(jwtTokenProvider.validateJwtToken(token), "Token should be valid");
        assertEquals("testUser", jwtTokenProvider.getUsernameFromJwt(token), "Username should match");
    }

    @Test
    void testGenerateAndValidateRefreshToken() {
        String refreshToken = jwtTokenProvider.generateJwtRefreshToken("testUser");

        logger.info("Generated JWT refresh token: {}", refreshToken);
        assertNotNull(refreshToken, "Refresh token should not be null");
        assertTrue(jwtTokenProvider.validateJwtRefreshToken(refreshToken), "Refresh token should be valid");
        assertEquals("testUser", jwtTokenProvider.getUsernameFromJwtRefreshToken(refreshToken), "Username should match");
    }

    @Test
    void testValidateExpiredJwtToken() throws InterruptedException {
        JwtTokenProvider shortLivedTokenProvider = new JwtTokenProvider(
                "VGhpcyBpcyBhIHZlcnkgc2VjdXJlIGtleSBmb3IgdGVzdGluZy4=",
                "VGhpcyBpcyBhIHZlcnkgc2VjdXJlIGtleSBmb3IgdGVzdGluZy4=",
                1, // Expiration in 1ms for testing
                86400000
        );

        String token = shortLivedTokenProvider.generateJwtToken("testUser");
        Thread.sleep(10); // Wait for the token to expire

        logger.info("Testing expired JWT token validation...");
        assertThrows(ExpiredJwtException.class, () -> shortLivedTokenProvider.validateJwtToken(token), "Expired token should throw exception");
    }
}
