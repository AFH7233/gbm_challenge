package com.afh.gbm.configuration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;


/**
 * Configuration and entry point for Account service.
 *
 * @author Andres Fuentes Hernandez
 */
@SpringBootApplication(scanBasePackages = "com.afh.gbm", exclude = SecurityAutoConfiguration.class)
public class AccountServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AccountServiceApplication.class, args);
    }
}
