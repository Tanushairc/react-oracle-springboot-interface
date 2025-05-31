package com.example.userapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Main application class for Spring Boot
 * This is the entry point of the application
 */
// @SpringBootApplication is a convenience annotation that combines:
// - @Configuration: Marks this class as a source of bean definitions
// - @EnableAutoConfiguration: Enables Spring Boot's auto-configuration
// - @ComponentScan: Scans for components in current package and sub-packages
@SpringBootApplication
// @EnableTransactionManagement enables Spring's annotation-driven transaction management
// This allows @Transactional annotations to work properly
@EnableTransactionManagement
public class UserManagementApiApplication {

    /**
     * Main method - Entry point of the Spring Boot application
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        // SpringApplication.run() method:
        // 1. Creates an ApplicationContext
        // 2. Registers all beans
        // 3. Starts the embedded Tomcat server
        // 4. Initializes all auto-configurations
        // 5. Runs any CommandLineRunner or ApplicationRunner beans
        SpringApplication.run(UserManagementApiApplication.class, args);
        
        // The application will start on port 8080 (configured in application.properties)
        // REST API endpoints will be available at: http://localhost:8080/api/users
        
        System.out.println("User Management API started successfully!");
        System.out.println("API Base URL: http://localhost:8080/api/users");
        System.out.println("Health Check: http://localhost:8080/actuator/health");
    }
}