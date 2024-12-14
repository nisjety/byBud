package com.bybud.common;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.bybud.common.repository")
@EntityScan(basePackages = "com.bybud.common.model")
public class CommonModuleConfig {
}
