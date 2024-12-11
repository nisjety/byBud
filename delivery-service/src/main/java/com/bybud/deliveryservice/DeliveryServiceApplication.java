package com.bybud.deliveryservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.bybud.deliveryservice", "com.bybud.common"})
public class DeliveryServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(DeliveryServiceApplication.class, args);
    }
}
