package com.bybud.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtConfig {

    @Value("${JWT_SECRET}")
    private String jwtSecret;

    @Value("${JWT_REFRESH_SECRET}")
    private String jwtRefreshSecret;

    @Value("${JWT_EXPIRATION_MS}")
    private long jwtExpirationMs;

    @Value("${JWT_REFRESH_EXPIRATION_MS}")
    private long jwtRefreshExpirationMs;

    public String getJwtSecret() {
        return jwtSecret;
    }

    public String getJwtRefreshSecret() {
        return jwtRefreshSecret;
    }

    public long getJwtExpirationMs() {
        return jwtExpirationMs;
    }

    public long getJwtRefreshExpirationMs() {
        return jwtRefreshExpirationMs;
    }
}
