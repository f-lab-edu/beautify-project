package com.bp.app.api.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "thread-pool.async" )
public record AsyncThreadPoolConfigurationProperties(Integer corePoolSize, Integer maxPoolSize,
                                                     Integer queueCapacity, String threadNamePrefix) {
}
