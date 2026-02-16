package com.qfang.hackbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Main Spring Boot application with performance optimizations enabled.
 * 
 * Performance features:
 * - @EnableCaching: Enables application-level caching
 * - @EnableAsync: Enables asynchronous processing for non-blocking operations
 */
@SpringBootApplication
@EnableCaching
@EnableAsync
public class HackbotApplication {

    public static void main(String[] args) {
        SpringApplication.run(HackbotApplication.class, args);
    }
}
