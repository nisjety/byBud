package com.bybud.common.client;

import com.bybud.common.dto.DeliveryDTO;
import com.bybud.common.dto.UserDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class DeliveryServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(DeliveryServiceClient.class);
    private final RestTemplate restTemplate;
    private final String deliveryServiceBaseUrl;
    private final String userServiceBaseUrl;

    public DeliveryServiceClient(RestTemplate restTemplate,
                                 @Value("${delivery-service.url}") String deliveryServiceUrl,
                                 @Value("${user-service.url}") String userServiceUrl) {
        this.restTemplate = restTemplate;
        this.deliveryServiceBaseUrl = deliveryServiceUrl;
        this.userServiceBaseUrl = userServiceUrl;
    }



    @Retryable(retryFor = Exception.class, backoff = @Backoff(delay = 3000))
    public DeliveryDTO createDelivery(DeliveryDTO deliveryDTO) {
        String url = deliveryServiceBaseUrl + "/api/delivery";
        logger.info("Creating a new delivery: {}", deliveryDTO);
        HttpHeaders headers = createHeaders();
        HttpEntity<DeliveryDTO> entity = new HttpEntity<>(deliveryDTO, headers);

        return sendRequest(url, HttpMethod.POST, entity);
    }

    @Retryable(retryFor = Exception.class, backoff = @Backoff(delay = 3000))
    public List<DeliveryDTO> getAllDeliveries() {
        String url = deliveryServiceBaseUrl + "/api/delivery";
        logger.info("Fetching all deliveries.");
        HttpHeaders headers = createHeaders();
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<List<DeliveryDTO>> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity,
                    new ParameterizedTypeReference<List<DeliveryDTO>>() {
                    });
            return response.getBody();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        }
    }

    @Retryable(retryFor = Exception.class, backoff = @Backoff(delay = 3000))
    public UserDTO getUserById(Long userId) {
        String url = userServiceBaseUrl + "/api/users/" + userId;
        logger.info("Fetching user with ID: {}", userId);

        HttpHeaders headers = createHeaders();
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            // Fetch the response as a Map to extract "data"
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            // Extract the "data" field and map it to UserDTO
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("data")) {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.convertValue(responseBody.get("data"), UserDTO.class);
            } else {
                throw new IllegalArgumentException("User data not found in the response.");
            }
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        }
    }


    private DeliveryDTO sendRequest(String url, HttpMethod method, HttpEntity<?> entity) {
        try {
            ResponseEntity<DeliveryDTO> response = restTemplate.exchange(url, method, entity, DeliveryDTO.class);
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
