package com.bybud.userservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserHealthCheckController {
    @GetMapping("/health")
    public String healthCheck() {
        return "UserService is running";
    }
}
