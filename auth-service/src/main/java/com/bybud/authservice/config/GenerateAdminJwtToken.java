package com.bybud.authservice.config;

import com.bybud.common.security.JwtTokenProvider;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GenerateAdminJwtToken {

    private final JwtTokenProvider jwtTokenProvider;

    public GenerateAdminJwtToken(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Bean
    CommandLineRunner generateAdminToken() {
        return args -> {
            String adminToken = jwtTokenProvider.generateJwtToken("admin");
            System.out.println("Generated JWT Token for Admin:");
            System.out.println("Bearer " + adminToken);
        };
    }
}
