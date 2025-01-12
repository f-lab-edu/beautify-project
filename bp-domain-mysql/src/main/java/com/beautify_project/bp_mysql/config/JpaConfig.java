package com.beautify_project.bp_mysql.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = {"com.beautify_project.bp_mysql.repository"})
@EntityScan(basePackages = {"com.beautify_project.bp_mysql.entity"})
public class JpaConfig {

}
