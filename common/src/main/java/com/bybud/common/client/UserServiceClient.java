package com.bybud.common.client;

import com.bybud.common.dto.CreateUserDTO;
import com.bybud.common.dto.UserDTO;
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
public class UserServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceClient.class);
    private static final String USERS_API_BASE_PATH = "/api/users";

    private final RestTemplate restTemplate;
    private final String userServiceBaseUrl;

    public UserServiceClient(RestTemplate restTemplate,
                             @Value("${user-service.url}") String userServiceUrl) {
        this.restTemplate = restTemplate;
        this.userServiceBaseUrl = userServiceUrl;

        if (userServiceUrl == null || userServiceUrl.isBlank()) {
            throw new IllegalStateException("user-service.url must be configured");
        }
    }

    @Retryable(retryFor = Exception.class, backoff = @Backoff(delay = 3000))
    public UserDTO getUserById(Long id) {
        String url = String.format("%s%s/%d", userServiceBaseUrl, USERS_API_BASE_PATH, id);
        logger.info("Fetching user by ID: {}", id);
        return sendRequest(url, HttpMethod.GET, null);
    }

    @Retryable(retryFor = Exception.class, backoff = @Backoff(delay = 3000))
    public UserDTO createUser(CreateUserDTO createUserDTO) {
        String url = userServiceBaseUrl + USERS_API_BASE_PATH;
        logger.info("Creating user with data: {}", createUserDTO);
        HttpEntity<CreateUserDTO> requestEntity = new HttpEntity<>(createUserDTO, createHeaders());
        return sendRequest(url, HttpMethod.POST, requestEntity);
    }

    @Retryable(retryFor = Exception.class, backoff = @Backoff(delay = 3000))
    public UserDTO getUserDetails(String usernameOrEmail) {
        String url = String.format("%s%s/details?usernameOrEmail=%s", userServiceBaseUrl, USERS_API_BASE_PATH, usernameOrEmail);
        logger.info("Fetching user details for: {}", usernameOrEmail);
        return sendRequest(url, HttpMethod.GET, null);
    }

    public boolean isUserServiceAvailable() {
        String url = userServiceBaseUrl + "/api/health";
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            logger.warn("UserService health check failed: {}", e.getMessage());
            return false;
        }
    }

    private UserDTO sendRequest(String url, HttpMethod method, HttpEntity<?> requestEntity) {
        try {
            ResponseEntity<UserDTO> response = restTemplate.exchange(url, method, requestEntity, UserDTO.class);
            return response.getBody();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP Error - Status: {}, ResponseBody: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        } catch (Exception e) {
            logger.error("Error sending request to UserService: {}", e.getMessage());
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
