package com.bp.app.event.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication(scanBasePackages = {"com.bp"})
@ConfigurationPropertiesScan("com.bp.kafka.event.consumer.config.properties")
public class BpKafkaEventConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(BpKafkaEventConsumerApplication.class, args);
    }
}
