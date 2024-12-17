package com.bybud.deliveryservice.config;

import com.bybud.common.repository.UserRepository;
import com.bybud.common.security.AuthTokenFilter;
import com.bybud.common.security.JwtTokenProvider;
import com.bybud.common.security.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DeliveryServiceConfig {

    @Value("${bybud.app.jwtSecret}")
    private String jwtSecret;

    @Value("${bybud.app.jwtRefreshSecret}")
    private String jwtRefreshSecret;

    @Value("${bybud.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    @Value("${bybud.app.jwtRefreshExpirationMs}")
    private int jwtRefreshExpirationMs;

    @Bean
    public UserDetailsServiceImpl userDetailsServiceImpl(UserRepository userRepository) {
        return new UserDetailsServiceImpl(userRepository);
    }

    @Bean
    public JwtTokenProvider jwtTokenProvider() {
        return new JwtTokenProvider(jwtSecret, jwtRefreshSecret, jwtExpirationMs, jwtRefreshExpirationMs);
    }

    @Bean
    public AuthTokenFilter authTokenFilter(UserDetailsServiceImpl userDetailsServiceImpl, JwtTokenProvider jwtTokenProvider) {
        return new AuthTokenFilter(userDetailsServiceImpl, jwtTokenProvider);
    }
}
