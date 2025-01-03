package com.bybud.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${cors.allowed-origins:http://localhost:5173}") // Default to localhost:5173
    private String[] allowedOrigins;

    @Value("${cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS}") // Default HTTP methods
    private String[] allowedMethods;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // or any other valid path pattern
                .allowedOrigins(allowedOrigins)
                .allowedMethods(allowedMethods)
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
