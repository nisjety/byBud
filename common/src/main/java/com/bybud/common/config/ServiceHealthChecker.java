package com.bybud.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ServiceHealthChecker {

    private final RestTemplate restTemplate;
    private final String authServiceUrl;
    private final String userServiceUrl;
    private final String deliveryServiceUrl;

    public ServiceHealthChecker(
            RestTemplate restTemplate,
            @Value("${auth-service.url}") String authServiceUrl,
            @Value("${user-service.url}") String userServiceUrl,
            @Value("${delivery-service.url}") String deliveryServiceUrl) {
        this.restTemplate = restTemplate;
        this.authServiceUrl = authServiceUrl;
        this.userServiceUrl = userServiceUrl;
        this.deliveryServiceUrl = deliveryServiceUrl;
    }

    @Scheduled(fixedRate = 30000) // Every 30 seconds
    public void checkHealth() {
        checkService(authServiceUrl + "/api/auth/health", "AuthService");
        checkService(userServiceUrl + "/api/user/health", "UserService");
        checkService(deliveryServiceUrl + "/api/delivery/health", "DeliveryService");
    }

    private void checkService(String url, String serviceName) {
        try {
            String response = restTemplate.getForObject(url, String.class);
            System.out.println(serviceName + " is available: " + response);
        } catch (Exception e) {
            System.err.println(serviceName + " is unavailable: " + e.getMessage());
        }
    }
}
