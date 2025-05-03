package com.bp.cache.config;

import com.bp.cache.config.properties.RedisConfigurationProperties;
import com.bp.cache.serializer.RedisCompressionSerializer;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@AutoConfiguration
@ConditionalOnClass({RedisConnectionFactory.class, RedisCacheManager.class})
@ConditionalOnProperty(name = "cache.redis.enabled", havingValue = "true", matchIfMissing = false)
@EnableConfigurationProperties(RedisConfigurationProperties.class)
@EnableCaching
public class RedisAutoConfiguration {

    private static final int DEFAULT_COMPRESSION_THRESHOLD_BYTES = 1024;
    private static final Duration DEFAULT_TTL = Duration.ofMinutes(30);

    @Bean
    @ConditionalOnMissingBean
    public RedisConnectionFactory redisConnectionFactory(RedisConfigurationProperties properties) {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(properties.host());
        config.setPort(properties.port());
        config.setPassword(properties.password());

        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
            .commandTimeout(properties.timeout()).build();

        return new LettuceConnectionFactory(config, clientConfig);
    }

    @Bean
    @ConditionalOnMissingBean
    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory,
        Map<String, RedisCacheConfiguration> redisCacheConfigurationByName) {

        return RedisCacheManager.builder(redisConnectionFactory).cacheDefaults(
                createCacheConfiguration(DEFAULT_TTL, DEFAULT_COMPRESSION_THRESHOLD_BYTES))
            .withInitialCacheConfigurations(redisCacheConfigurationByName).build();
    }

    @Bean
    @ConditionalOnMissingBean
    public Map<String, RedisCacheConfiguration> redisCacheConfigurationByCacheName(
        RedisConfigurationProperties properties) {

        Map<String, RedisCacheConfiguration> redisCacheConfigurationByName = new HashMap<>();

        properties.configsByName().forEach((name, config) -> {
            redisCacheConfigurationByName.put(name,
                createCacheConfiguration(config.ttl(), config.compressionThresholdBytes()));
        });

        return redisCacheConfigurationByName;
    }

    private RedisCacheConfiguration createCacheConfiguration(Duration ttl, int compressionThresholdBytes) {
        RedisSerializer<String> keySerializer = new StringRedisSerializer();
        RedisSerializer<?> valueSerializer = new RedisCompressionSerializer<>(
            new GenericJackson2JsonRedisSerializer(), compressionThresholdBytes);

        return RedisCacheConfiguration.defaultCacheConfig().entryTtl(ttl).serializeKeysWith(
                RedisSerializationContext.SerializationPair.fromSerializer(keySerializer))
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer));
    }
}
