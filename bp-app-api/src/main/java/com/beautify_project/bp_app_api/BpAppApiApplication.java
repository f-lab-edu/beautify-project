package com.beautify_project.bp_app_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication(scanBasePackages = {"com.beautify_project"})
@ConfigurationPropertiesScan("com.beautify_project.bp_app_api.config")
public class BpAppApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(BpAppApiApplication.class, args);
	}

}
