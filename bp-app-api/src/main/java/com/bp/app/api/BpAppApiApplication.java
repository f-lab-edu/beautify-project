package com.bp.app.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication(scanBasePackages = {"com.bp"})
@ConfigurationPropertiesScan("com.bp.app.api.config")
public class BpAppApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(BpAppApiApplication.class, args);
    }

}
