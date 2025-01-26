package com.bp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication(scanBasePackages = {"com.bp"})
@ConfigurationPropertiesScan("com.bp")
public class BpTestContainerApplication {

    public static void main(String[] args) {
        SpringApplication.run(BpTestContainerApplication.class, args);
    }

}
