package com.bp.domain.mysql.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = {"com.bp.domain.mysql.repository"})
@EntityScan(basePackages = {"com.bp.domain.mysql.entity"})
public class JpaConfig {

}
