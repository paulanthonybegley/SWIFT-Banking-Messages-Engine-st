package com.qoomon.banking.swift.ui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot application class for SWIFT Banking Messages UI.
 * Uses Thymeleaf for templating and Spring MVC for web functionality.
 */
@SpringBootApplication
public class Application {
    
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}