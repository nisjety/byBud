package com.bybud.common.client;

import com.bybud.common.dto.UserDTO;
import com.bybud.common.dto.CreateUserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import jakarta.annotation.PostConstruct;

import java.util.Collections;
import java.util.Objects;

@Component
public class UserServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceClient.class);
    private static final String USERS_API_BASE_PATH = "/api/users";

    private final RestTemplate restTemplate;
    private final String userServiceBaseUrl;
    private final TimeoutSettings timeoutSettings;

    public UserServiceClient(RestTemplate restTemplate,
                             @Value("${user-service.url}") String userServiceUrl,
                             @Value("${rest.template.connectTimeout:5000}") int connectTimeout,
                             @Value("${rest.template.readTimeout:5000}") int readTimeout) {
        if (userServiceUrl == null || userServiceUrl.isBlank()) {
            throw new IllegalStateException("user-service.url must be configured");
        }
        this.restTemplate = restTemplate;
        this.userServiceBaseUrl = userServiceUrl;
        this.timeoutSettings = new TimeoutSettings(connectTimeout, readTimeout);
    }

    @PostConstruct
    private void initializeRestTemplate() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(timeoutSettings.connectTimeout());
        requestFactory.setReadTimeout(timeoutSettings.readTimeout());
        restTemplate.setRequestFactory(requestFactory);
    }

    @Retryable(retryFor = Exception.class, backoff = @Backoff(delay = 5000))
    public UserDTO getUserById(Long id) {
        String url = String.format("%s%s/%d", userServiceBaseUrl, USERS_API_BASE_PATH, id);
        logger.info("Fetching user by ID: {}", id);
        return executeGetUserRequest(url, HttpMethod.GET, null, "fetching user by ID " + id);
    }

    @Retryable(retryFor = Exception.class, backoff = @Backoff(delay = 5000))
    public UserDTO createUser(CreateUserDTO createUserDTO) {
        String url = userServiceBaseUrl + USERS_API_BASE_PATH;
        logger.info("Sending request to User-Service to create user: {}", createUserDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<CreateUserDTO> entity = new HttpEntity<>(createUserDTO, headers);

        try {
            ResponseEntity<UserDTO> response = restTemplate.exchange(url, HttpMethod.POST, entity, UserDTO.class);
            return response.getBody();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error during creating user: Status={}, ResponseBody={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        }
    }

    @Retryable(retryFor = Exception.class, backoff = @Backoff(delay = 5000))
    public UserDTO getUserDetails(String usernameOrEmail) {
        String url = String.format("%s%s/details?usernameOrEmail=%s", userServiceBaseUrl, USERS_API_BASE_PATH, usernameOrEmail);
        logger.info("Fetching user details for: {}", usernameOrEmail);
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        return executeGetUserRequest(url, HttpMethod.GET, requestEntity, "fetching user details for " + usernameOrEmail);
    }

    public boolean isUserServiceAvailable() {
        String url = userServiceBaseUrl + "/api/health";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class);
            logger.info("UserService health check response: {}", response.getStatusCode());
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            logger.warn("UserService health check failed: {}", e.getMessage());
            return false;
        }
    }

    private UserDTO executeGetUserRequest(String url, HttpMethod method, HttpEntity<?> entity, String action) {
        try {
            logger.info("Sending {} request to: {}", method, url);
            ResponseEntity<UserDTO> response = restTemplate.exchange(url, method, entity, UserDTO.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                logger.error("Non-2xx response during {}: Status={} Body={}", action, response.getStatusCode(), response.getBody());
                throw new IllegalStateException("Non-2xx response received.");
            }

            return response.getBody();
        } catch (HttpStatusCodeException e) {
            String payload = entity != null ? Objects.toString(entity.getBody()) : "N/A";
            logger.error("HTTP error during {}: URL={}, Payload={}, Status={}, ResponseBody={}",
                    action, url, payload, e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw e;
        } catch (Exception e) {
            String payload = entity != null ? Objects.toString(entity.getBody()) : "N/A";
            logger.error("Error during {}: URL={}, Payload={}, Message={}", action, url, payload, e.getMessage(), e);
            throw e;
        }
    }

    private record TimeoutSettings(int connectTimeout, int readTimeout) {
    }
}
