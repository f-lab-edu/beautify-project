package com.beautify_project.bp_kafka_event_consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication(scanBasePackages = {"com.beautify_project"})
@ConfigurationPropertiesScan("com.beautify_project.bp_kafka_event_consumer.config.properties")
public class BpKafkaEventConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(BpKafkaEventConsumerApplication.class, args);
    }
}
