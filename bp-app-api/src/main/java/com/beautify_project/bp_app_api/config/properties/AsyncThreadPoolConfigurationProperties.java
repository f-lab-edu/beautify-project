package com.beautify_project.bp_app_api.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "thread-pool.async" )
public record AsyncThreadPoolConfigurationProperties(Integer corePoolSize, Integer maxPoolSize,
                                                     Integer queueCapacity, String threadNamePrefix) {
}
