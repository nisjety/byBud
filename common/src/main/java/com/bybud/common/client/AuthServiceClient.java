package com.bybud.common.client;

import com.bybud.common.dto.UserDTO;
import com.bybud.common.dto.LoginRequest;
import com.bybud.common.dto.RegisterRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Component
public class AuthServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceClient.class);
    private final RestTemplate restTemplate;
    private final String authServiceBaseUrl;

    public AuthServiceClient(RestTemplate restTemplate,
                             @Value("${auth-service.url}") String authServiceUrl) {
        this.restTemplate = restTemplate;
        this.authServiceBaseUrl = authServiceUrl;
    }

    @Retryable(retryFor = Exception.class, backoff = @Backoff(delay = 3000))
    public UserDTO registerUser(RegisterRequest request) {
        String url = authServiceBaseUrl + "/api/auth/register";
        logger.info("Registering a new user: {}", request);
        HttpEntity<RegisterRequest> entity = new HttpEntity<>(request, createHeaders());

        return sendRequest(url, HttpMethod.POST, entity);
    }

    @Retryable(retryFor = Exception.class, backoff = @Backoff(delay = 3000))
    public UserDTO loginUser(LoginRequest request) {
        String url = authServiceBaseUrl + "/api/auth/login";
        logger.info("Logging in user: {}", request.getUsernameOrEmail());
        HttpEntity<LoginRequest> entity = new HttpEntity<>(request, createHeaders());

        return sendRequest(url, HttpMethod.POST, entity);
    }

    private UserDTO sendRequest(String url, HttpMethod method, HttpEntity<?> entity) {
        try {
            ResponseEntity<UserDTO> response = restTemplate.exchange(url, method, entity, UserDTO.class);
            return response.getBody();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        }
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }
}
