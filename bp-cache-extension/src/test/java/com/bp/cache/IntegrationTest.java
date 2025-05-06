package com.bp.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bp.cache.IntegrationTest.TestRedisCacheConfig;
import com.bp.cache.config.properties.RedisConfigurationProperties;
import com.bp.cache.config.properties.RedisConfigurationProperties.CacheConfig;
import com.bp.cache.serializer.RedisCompressionSerializer;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestRedisCacheConfig.class)
public class IntegrationTest {

    static final int REDIS_PORT = 6379;

    @Container
    static final GenericContainer<?> REDIS_CONTAINER = new GenericContainer<>(
        "redis:7-alpine").withExposedPorts(REDIS_PORT);

    @Autowired
    private RedisCacheManager cacheManager;

    @Autowired
    private RedisConnectionFactory connectionFactory;

    @Test
    @DisplayName("threshold 보다 큰 데이터는 redis 에 GZIP 포맷으로 저장되어야 한다.")
    void storedInGZIPIfDataIsBiggerThanThreshold() {
        // given
        String key = "testKey";
        String value = "ABC".repeat(500);

        Cache cache = cacheManager.getCache(TestRedisCacheConfig.DEFAULT_CACHE_NAME);
        assertNotNull(cache);

        // when
        cache.put(key, value);

        // then
        try (RedisConnection redisConnection = connectionFactory.getConnection()) {
            byte[] rawValue = redisConnection.get(
                (TestRedisCacheConfig.DEFAULT_CACHE_NAME + "::" + key).getBytes(
                    StandardCharsets.UTF_8));

            assertNotNull(rawValue);
            assertTrue(isCompressed(rawValue));
        }
    }

    @Test
    @DisplayName("threshold 보다 작은 데이터는 redis 에 GZIP 포멧으로 저장되면 안된다.")
    void notStoredInGZIPIfDataIsSmallerThanThreshold() {
        // given
        String key = "testKey";
        String value = "ABC";

        Cache cache = cacheManager.getCache(TestRedisCacheConfig.DEFAULT_CACHE_NAME);
        assertNotNull(cache);

        // when
        cache.put(key, value);

        // then
        try (RedisConnection connection = connectionFactory.getConnection()) {
            byte[] rawValue = connection.get(
                (TestRedisCacheConfig.DEFAULT_CACHE_NAME + "::" + key).getBytes(
                    StandardCharsets.UTF_8)
            );

            assertNotNull(rawValue);
            assertFalse(isCompressed(rawValue));
        }
    }

    @Test
    @DisplayName("threshold 보다 큰 데이터는 압축해서 redis 에 저장되고 조회(복구) 가능해야 한다.")
    void storedAndRecoverableInRedisIfDataIsBiggerThanThreshold() {
        // given
        String key = "testKey";
        String value = "ABC".repeat(500);

        Cache cache = cacheManager.getCache(TestRedisCacheConfig.DEFAULT_CACHE_NAME);
        assertNotNull(cache);

        // when
        cache.put(key, value);
        String cachedValue = cache.get(key, String.class);

        // then
        assertEquals(cachedValue, value);
    }


    @Test
    @DisplayName("캐시 이름별로 설정된 threshold 값에 따라 압축이 되어야 한다")
    void compressByThreshold() {
        // given
        String smallKey = "smallKey";
        String smallValue = "ABC".repeat(100);

        String bigKey = "bigKey";
        String bigValue = "ABC".repeat(500);

        Cache smallCache = cacheManager.getCache(TestRedisCacheConfig.SMALL_CACHE_NAME);
        Cache bigCache = cacheManager.getCache(TestRedisCacheConfig.BIG_CACHE_NAME);

        assertNotNull(smallCache);
        assertNotNull(bigCache);

        // when
        smallCache.put(smallKey, smallValue);
        bigCache.put(bigKey, bigValue);

        // then
        try (RedisConnection redisConnection = connectionFactory.getConnection()) {
            byte[] smallRawValue = redisConnection.get(
                (TestRedisCacheConfig.SMALL_CACHE_NAME + "::" + smallKey).getBytes(
                    StandardCharsets.UTF_8));

            byte[] bigRawValue = redisConnection.get(
                (TestRedisCacheConfig.BIG_CACHE_NAME + "::" + bigKey).getBytes(
                    StandardCharsets.UTF_8));

            assertNotNull(smallRawValue);
            assertNotNull(bigRawValue);

            assertTrue(isCompressed(smallRawValue));
            assertTrue(isCompressed(bigRawValue));
        }
    }

    @Test
    @DisplayName("캐시 이름별로 설정된 threshold 값에 따라 압축이 되면 안된다.")
    void notCompressByThreshold() {
        // given
        String smallKey = "smallKey";
        String bigKey = "bigKey";
        String smallValue = "ABC";
        String bigValue = "ABC";

        Cache smallCache = cacheManager.getCache(TestRedisCacheConfig.SMALL_CACHE_NAME);
        Cache bigCache = cacheManager.getCache(TestRedisCacheConfig.BIG_CACHE_NAME);

        assertNotNull(smallCache);
        assertNotNull(bigCache);

        // when
        smallCache.put(smallKey, smallValue);
        bigCache.put(bigKey, bigValue);

        // then
        try (RedisConnection connection = connectionFactory.getConnection()) {
            byte[] smallRawValue = connection.get(
                (TestRedisCacheConfig.SMALL_CACHE_NAME + "::" + smallKey).getBytes(
                    StandardCharsets.UTF_8));

            byte[] bigRawValue = connection.get(
                (TestRedisCacheConfig.BIG_CACHE_NAME + "::" + bigKey).getBytes(
                    StandardCharsets.UTF_8));

            assertNotNull(smallRawValue);
            assertNotNull(bigRawValue);

            assertFalse(isCompressed(smallRawValue));
            assertFalse(isCompressed(bigRawValue));
        }
    }

    @Test
    @DisplayName("TTL 이 지난 캐시 데이터는 삭제되어야 한다.")
    void deletedIfCachedDataExceedsTTL() throws Exception {
        // given
        String shortTtlKey = "shortTtlKey";
        String shortTtlValue = "123";

        Cache shortTtlCache = cacheManager.getCache(TestRedisCacheConfig.SHORT_TTL_CACHE_NAME);

        assertNotNull(shortTtlCache);

        // when
        shortTtlCache.put(shortTtlKey, shortTtlValue);

        Thread.sleep(1000); // ttl 1초

        // then
        try (RedisConnection connection = connectionFactory.getConnection()) {
            byte[] shortTtlRawValue = connection.get(
                (TestRedisCacheConfig.SHORT_TTL_CACHE_NAME + "::" + shortTtlKey).getBytes(
                    StandardCharsets.UTF_8));
            assertNull(shortTtlRawValue);
        }
    }

    private boolean isCompressed(byte[] bytes) {
        return bytes != null && bytes.length >= 2 && isGzipHeader(bytes);
    }

    private boolean isGzipHeader(byte[] bytes) {
        return bytes[0] == (byte) 0x1f && bytes[1] == (byte) 0x8b;
    }

    @Configuration
    @EnableCaching
    static class TestRedisCacheConfig {
        static final int DEFAULT_COMPRESSION_THRESHOLD_BYTES = 100;
        static final Duration DEFAULT_TTL = Duration.ofMinutes(1);
        static final String DEFAULT_CACHE_NAME = "testCache";
        static final String BIG_CACHE_NAME = "bigCache";
        static final String SMALL_CACHE_NAME = "smallCache";
        static final String SHORT_TTL_CACHE_NAME = "shortTtlCache";

        @Bean
        RedisConnectionFactory redisConnectionFactory() {
            String host = REDIS_CONTAINER.getHost();
            int port = REDIS_CONTAINER.getMappedPort(REDIS_PORT);
            return new LettuceConnectionFactory(host, port);
        }

        @Bean
        RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory,
            Map<String, RedisCacheConfiguration> redisCacheConfigurationByName) {

            return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(createCacheConfiguration(DEFAULT_TTL,
                    DEFAULT_COMPRESSION_THRESHOLD_BYTES))
                .withInitialCacheConfigurations(redisCacheConfigurationByName).build();
        }

        @Bean
        public Map<String, RedisCacheConfiguration> redisCacheConfigurationByCacheName(
            RedisConfigurationProperties properties) {

            Map<String, RedisCacheConfiguration> redisCacheConfigurationByName = new HashMap<>();

            properties.configsByName().forEach((name, config) -> {
                redisCacheConfigurationByName.put(name,
                    createCacheConfiguration(config.ttl(), config.compressionThresholdBytes()));
            });

            return redisCacheConfigurationByName;
        }

        @Bean
        RedisConfigurationProperties properties() {
            Map<String, CacheConfig> configsByName = new HashMap<>();
            CacheConfig testConfig = new CacheConfig(100, Duration.ofHours(1));
            configsByName.put(DEFAULT_CACHE_NAME, testConfig);

            CacheConfig bigThresholdCacheConfig = new CacheConfig(1024, Duration.ofHours(1));
            configsByName.put(BIG_CACHE_NAME, bigThresholdCacheConfig);
            CacheConfig smallThresholdCacheConfig = new CacheConfig(100, Duration.ofHours(1));
            configsByName.put(SMALL_CACHE_NAME, smallThresholdCacheConfig);
            CacheConfig shortTtlCacheConfig = new CacheConfig(DEFAULT_COMPRESSION_THRESHOLD_BYTES,
                Duration.ofSeconds(1));
            configsByName.put(SHORT_TTL_CACHE_NAME, shortTtlCacheConfig);

            return new RedisConfigurationProperties(REDIS_CONTAINER.getHost(),
                REDIS_CONTAINER.getMappedPort(REDIS_PORT), "password", Duration.ofSeconds(5),
                configsByName);
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
}
