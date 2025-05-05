package com.bp.cache.config.properties;

import java.time.Duration;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * cache-extension 관련 설정값
 */
@ConfigurationProperties(prefix = "cache.redis")
public record RedisConfigurationProperties(
    String host,
    int port,
    String password,
    Duration timeout,
    Map<String, CacheConfig> configsByName
) {

    public record CacheConfig(
        int compressionThresholdBytes,
        Duration ttl
    ) { }

}
