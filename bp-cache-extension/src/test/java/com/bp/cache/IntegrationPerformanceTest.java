package com.bp.cache;

import com.bp.cache.IntegrationPerformanceTest.PerformanceTestRedisCacheConfig;
import com.bp.cache.config.properties.RedisConfigurationProperties;
import com.bp.cache.config.properties.RedisConfigurationProperties.CacheConfig;
import com.bp.cache.serializer.RedisCompressionSerializer;
import com.sun.management.OperatingSystemMXBean;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.junit.jupiter.api.Disabled;
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
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Disabled
@Testcontainers
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = PerformanceTestRedisCacheConfig.class)
public class IntegrationPerformanceTest {

    static final int REDIS_PORT = 6379;

    @Container
    static final GenericContainer<?> REDIS_CONTAINER = new GenericContainer<>(
        "redis:7-alpine").withExposedPorts(REDIS_PORT);

    private static final Random random = new Random();
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(
        "yyyy-MM-dd HH:mm");
    private static final int BYTE_TO_KB = 1024;
    private static final int MAX_SIZE = BYTE_TO_KB * BYTE_TO_KB; // 1MB
    private static final int PERFORMANCE_TEST_ITERATIONS = 10; // 반복횟수
    private static final String TEST_KEY_PREFIX = "test:compression:";

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private LettuceConnectionFactory lettuceConnectionFactory;

    @Autowired
    private RedisCacheManager cacheManager;

    @Test
    @DisplayName("데이터 크기별 Redis 압축 성능 테스트")
    void testCompressionPerformanceWithRealRedis() throws Exception {
        // 테스트 결과를 저장할 리스트
        List<CompressionTestResult> results = new ArrayList<>();

        // 다양한 크기의 데이터에 대해 테스트 실행
        for (int size = 128; size <= MAX_SIZE; size *= 2) {
            CompressionTestResult result = testPerformance(size);
            results.add(result);
        }

        analyzeSummary(results);
    }

    private CompressionTestResult testPerformance(int dataSize) {
        // 테스트 데이터 생성
        String testData = generateJsonData(dataSize);
        String compressedCacheKey = TEST_KEY_PREFIX + "compressed:" + dataSize;
        String plainCacheKey = TEST_KEY_PREFIX + "plain:" + dataSize;

        int realTestDataSize = testData.getBytes(StandardCharsets.UTF_8).length;
        CompressionTestResult testResult = new CompressionTestResult(realTestDataSize);

        Cache compressedCache = cacheManager.getCache(PerformanceTestRedisCacheConfig.DEFAULT_CACHE_NAME);

        // 비압축 직렬화를 위한 템플릿 설정
        StringRedisTemplate plainRedisTemplate = redisTemplate;

        // 테스트 전 캐시 클리어
        redisTemplate.delete(redisTemplate.keys(TEST_KEY_PREFIX + "*"));

        // 압축 저장 성능 테스트
        testCompressionStorePerformance(compressedCacheKey, compressedCache, testData, testResult);

        // 압축 조회 성능 테스트
        testCompressionRetrievePerformance(compressedCache, compressedCacheKey, testData, testResult);

        // 비압축 저장 성능 테스트
        testPlainStorePerformance(plainCacheKey, plainRedisTemplate, testData, testResult);

        // 비압축 조회 성능 테스트
        testPlainRetrievePerformance(plainRedisTemplate, plainCacheKey, testData, testResult);

        // Redis 상에서의 메모리 사용량 비교
        compareMemoryUsageInRedis(compressedCacheKey, plainCacheKey, testResult);
        return testResult;
    }

    private void testCompressionStorePerformance(final String compressedCacheKey, final Cache compressedCache,
        final String testData, final CompressionTestResult testResult) {

        long totalCompressedWriteTime = 0;
        double totalCpuLoad = 0;

        for (int i = 0; i < PERFORMANCE_TEST_ITERATIONS; i++) {
            String uniqueKey = compressedCacheKey + ":" + i;

            double beforeCpu = getCpuLoad();
            long startTime = System.nanoTime();
            compressedCache.put(uniqueKey, testData);
            long endTime = System.nanoTime();
            double afterCpu = getCpuLoad();

            totalCompressedWriteTime += (endTime - startTime);
            totalCpuLoad += (beforeCpu + afterCpu) / 2;

            // 캐시 제거
            compressedCache.evict(uniqueKey);
        }
        testResult.compressedWriteTimeMs = totalCompressedWriteTime / (PERFORMANCE_TEST_ITERATIONS * 1_000_000.0);
        testResult.cpuLoadCompressedWrite = totalCpuLoad / PERFORMANCE_TEST_ITERATIONS;

        // 최종 저장 (메모리 사용량 측정용)
        compressedCache.put(compressedCacheKey, testData);
    }

    private void testCompressionRetrievePerformance(final Cache compressedCache, final String compressedCacheKey,
        final String testData, final CompressionTestResult testResult) {

        long totalCompressedReadTime = 0;
        double totalCpuLoad = 0;

        for (int i = 0; i < PERFORMANCE_TEST_ITERATIONS; i++) {
            double beforeCpu = getCpuLoad();
            long startTime = System.nanoTime();
            String value = compressedCache.get(compressedCacheKey, String.class);
            long endTime = System.nanoTime();
            double afterCpu = getCpuLoad();

            totalCompressedReadTime += (endTime - startTime);
            totalCpuLoad += (beforeCpu + afterCpu) / 2;

            // 데이터 정합성 검증
            if (i == 0 && !testData.equals(value)) {
                throw new AssertionError("압축 데이터 불일치: 원본 길이 = " + testData.length() + ", 조회 길이 = " + (value != null ? value.length() : "null"));
            }
        }
        testResult.compressedReadTimeMs = totalCompressedReadTime / (PERFORMANCE_TEST_ITERATIONS * 1_000_000.0);
        testResult.cpuLoadCompressedRead = totalCpuLoad / PERFORMANCE_TEST_ITERATIONS;
    }

    private void testPlainStorePerformance(final String plainCacheKey,
        final StringRedisTemplate plainRedisTemplate, final String testData,
        final CompressionTestResult testResult) {

        long totalPlainWriteTime = 0;
        double totalCpuLoad = 0;

        for (int i = 0; i < PERFORMANCE_TEST_ITERATIONS; i++) {
            String uniqueKey = plainCacheKey + ":" + i;

            double beforeCpu = getCpuLoad();
            long startTime = System.nanoTime();
            plainRedisTemplate.opsForValue().set(uniqueKey, testData);
            long endTime = System.nanoTime();
            double afterCpu = getCpuLoad();

            totalPlainWriteTime += (endTime - startTime);
            totalCpuLoad += (beforeCpu + afterCpu) / 2;

            // 캐시 제거
            plainRedisTemplate.delete(uniqueKey);
        }
        testResult.plainWriteTimeMs = totalPlainWriteTime / (PERFORMANCE_TEST_ITERATIONS * 1_000_000.0);
        testResult.cpuLoadPlainWrite = totalCpuLoad / PERFORMANCE_TEST_ITERATIONS;

        // 최종 저장 (메모리 사용량 측정용)
        plainRedisTemplate.opsForValue().set(plainCacheKey, testData);
    }

    private void testPlainRetrievePerformance(final StringRedisTemplate plainRedisTemplate,
        final String plainCacheKey, final String testData, final CompressionTestResult testResult) {

        long totalPlainReadTime = 0;
        double totalCpuLoad = 0;

        for (int i = 0; i < PERFORMANCE_TEST_ITERATIONS; i++) {

            double beforeCpu = getCpuLoad();
            long startTime = System.nanoTime();
            String value = plainRedisTemplate.opsForValue().get(plainCacheKey);
            long endTime = System.nanoTime();
            double afterCpu = getCpuLoad();

            totalPlainReadTime += (endTime - startTime);
            totalCpuLoad += (beforeCpu + afterCpu) / 2;

            // 데이터 정합성 검증
            if (i == 0 && !testData.equals(value)) {
                throw new AssertionError("비압축 데이터 불일치");
            }
        }
        testResult.plainReadTimeMs = totalPlainReadTime / (PERFORMANCE_TEST_ITERATIONS * 1_000_000.0);
        testResult.cpuLoadPlainRead = totalCpuLoad / PERFORMANCE_TEST_ITERATIONS;
    }

    private void compareMemoryUsageInRedis(final String compressedCacheKey, final String plainCacheKey,
        final CompressionTestResult testResult) {
        Long compressedMemoryUsage = getKeyMemoryUsage(compressedCacheKey);
        Long plainMemoryUsage = getKeyMemoryUsage(plainCacheKey);

        if (compressedMemoryUsage != null && plainMemoryUsage != null) {
            testResult.compressedMemoryBytes = compressedMemoryUsage;
            testResult.plainMemoryBytes = plainMemoryUsage;
            testResult.memorySavingPercentage =
                (1.0 - (double) compressedMemoryUsage / plainMemoryUsage) * 100.0;
        }
    }

    private Long getKeyMemoryUsage(String key) {
        try {
            return redisTemplate.execute((RedisCallback<Long>) connection -> {
                // 키 패턴으로 실제 키 찾기
                Set<String> matchingKeys = redisTemplate.keys("*" + key + "*");
                if (matchingKeys.isEmpty()) {
                    System.out.println("키를 찾을 수 없음: " + key);
                    return null;
                }

                String actualKey = matchingKeys.iterator().next();

                byte[] valueBytes = connection.stringCommands().get(actualKey.getBytes(StandardCharsets.UTF_8));
                if (valueBytes == null) {
                    return null;
                }

                return (long) valueBytes.length;
            });
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void analyzeSummary(List<CompressionTestResult> results) {
        System.out.println("\n========== 테스트 결과 =============");

        for (CompressionTestResult result : results) {
            System.out.println("\n================================");
            System.out.printf("데이터 크기: %d bytes (%.2f KB)\n", result.dataSize, result.dataSize / (double) BYTE_TO_KB);
            System.out.printf("압축된 데이터 크기: %d bytes (%.2f KB)\n", result.compressedMemoryBytes,
                result.compressedMemoryBytes / (double) BYTE_TO_KB);
            System.out.printf("메모리 절약(압축률): %.2f%%\n\n", result.memorySavingPercentage);

            System.out.printf("압축 저장 시간: %.3f ms\n", result.compressedWriteTimeMs);
            System.out.printf("압축 조회 시간: %.3f ms\n", result.compressedReadTimeMs);
            System.out.printf("비압축 저장 시간: %.3f ms\n", result.plainWriteTimeMs);
            System.out.printf("비압축 조회 시간: %.3f ms\n", result.plainReadTimeMs);
        }

        createAndSavePerformanceCharts(results);
    }

    private void createAndSavePerformanceCharts(final List<CompressionTestResult> results) {
        DefaultCategoryDataset writeTimeDataset = new DefaultCategoryDataset();
        DefaultCategoryDataset readTimeDataset = new DefaultCategoryDataset();
        DefaultCategoryDataset memorySavingDataset = new DefaultCategoryDataset();
        DefaultCategoryDataset cpuLoadDataset = new DefaultCategoryDataset();

        for (CompressionTestResult result : results) {
            String label = result.dataSize + "B";

            writeTimeDataset.addValue(Double.valueOf(result.compressedWriteTimeMs), "압축 저장(Write)", label);
            writeTimeDataset.addValue(Double.valueOf(result.plainWriteTimeMs), "비압축 저장(Write)", label);

            readTimeDataset.addValue(Double.valueOf(result.compressedReadTimeMs), "압축 읽기(Read)", label);
            readTimeDataset.addValue(Double.valueOf(result.plainReadTimeMs), "비압축 읽기(Read)", label);

            memorySavingDataset.addValue(Double.valueOf(result.memorySavingPercentage), "메모리 절약율", label);

            cpuLoadDataset.addValue(Double.valueOf(result.cpuLoadCompressedWrite * 100), "압축 저장(Write) CPU", label);
            cpuLoadDataset.addValue(Double.valueOf(result.cpuLoadPlainWrite * 100), "비압축 저장(Write) CPU", label);
        }

        JFreeChart writeTimeChart = ChartFactory.createLineChart(
            "쓰기 시간 비교", "데이터 크기", "Time (ms)", writeTimeDataset,
            PlotOrientation.VERTICAL, true, true, false
        );

        JFreeChart readTimeChart = ChartFactory.createLineChart(
            "읽기 시간 비교", "데이터 크기", "Time (ms)", readTimeDataset,
            PlotOrientation.VERTICAL, true, true, false
        );

        JFreeChart memorySavingChart = ChartFactory.createLineChart(
            "메모리 절약(압축률) (%)", "데이터 크기", "압축률 (%)", memorySavingDataset,
            PlotOrientation.VERTICAL, true, true, false
        );

        // 저장
        saveChartAsPNG(writeTimeChart, "write_time_chart.png", 800, 600);
        saveChartAsPNG(readTimeChart, "read_time_chart.png", 800, 600);
        saveChartAsPNG(memorySavingChart, "memory_saving_chart.png", 800, 600);
    }

    public static void saveChartAsPNG(JFreeChart chart, String fileName, int width, int height) {
        try {
            File outputFile = new File(fileName);
            ChartUtils.saveChartAsPNG(outputFile, chart, width, height);
            System.out.println("차트 저장 완료: " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    static class CompressionTestResult {
        final int dataSize;
        double compressedWriteTimeMs;
        double compressedReadTimeMs;
        double plainWriteTimeMs;
        double plainReadTimeMs;
        long compressedMemoryBytes;
        long plainMemoryBytes;
        double memorySavingPercentage;

        double cpuLoadCompressedWrite;
        double cpuLoadCompressedRead;
        double cpuLoadPlainWrite;
        double cpuLoadPlainRead;

        CompressionTestResult(int dataSize) {
            this.dataSize = dataSize;
        }
    }

    private double getCpuLoad() {
        OperatingSystemMXBean osBean2 = ManagementFactory.getPlatformMXBean(
            com.sun.management.OperatingSystemMXBean.class);
        return osBean2.getSystemCpuLoad() * 100;
    }

    public String generateJsonData(int byteSize) {
        StringBuilder sb = new StringBuilder(byteSize);

        while (sb.length() < byteSize) {
            String json = generateRandomJsonObject();
            sb.append(json);
        }

        String result = sb.substring(0, Math.min(byteSize, sb.length()));
        return result;
    }

    private String generateRandomJsonObject() {
        StringBuilder json = new StringBuilder();
        json.append("{");

        json.append("\"shopName\":\"").append(randomEngString(random.nextInt(10) + 5)).append("\",");
        json.append("\"userId\":\"").append(randomEngString(random.nextInt(8) + 3)).append("\",");
        json.append("\"reservationId\":").append(random.nextInt(100000)).append(",");

        LocalDateTime startTime = randomDateTime();
        LocalDateTime endTime = startTime.plusHours(random.nextInt(4) + 1); // 시작 시간 이후 1~4시간
        json.append("\"reservationStartTime\":\"").append(startTime.format(DATE_TIME_FORMATTER)).append("\",");
        json.append("\"reservationEndTime\":\"").append(endTime.format(DATE_TIME_FORMATTER)).append("\",");

        json.append("\"itemId\":").append(random.nextInt(100000)).append(",");
        json.append("\"description\":\"").append(randomMixedString(random.nextInt(30) + 10)).append("\"");

        json.append("}");
        return json.toString();
    }

    private LocalDateTime randomDateTime() {
        int year = 2023 + random.nextInt(3); // 2023~2025년
        int month = 1 + random.nextInt(12);
        int day = 1 + random.nextInt(28); //
        int hour = random.nextInt(24);
        int minute = random.nextBoolean() ? 0 : 30; // 00 또는 30분

        return LocalDateTime.of(year, month, day, hour, minute);
    }

    private String randomEngString(int length) {
        char[] engChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(engChars[random.nextInt(engChars.length)]);
        }
        return sb.toString();
    }

    private String randomMixedString(int length) {
        char[] engChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        char[] hangulChars = "가나다라마바사아자차카타파하거너더러머버서어저처커터퍼허".toCharArray();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            if (random.nextBoolean()) {
                sb.append(engChars[random.nextInt(engChars.length)]);
            } else {
                sb.append(hangulChars[random.nextInt(hangulChars.length)]);
            }
        }
        return sb.toString();
    }

    @Configuration
    @EnableCaching
    static class PerformanceTestRedisCacheConfig {

        static final int COMPRESSION_THRESHOLD_BYTES = 100;
        static final Duration DEFAULT_TTL = Duration.ofMinutes(1);
        static final String DEFAULT_CACHE_NAME = "testCache";

        @Bean
        RedisConnectionFactory redisConnectionFactory() {
            String host = REDIS_CONTAINER.getHost();
            int port = REDIS_CONTAINER.getMappedPort(REDIS_PORT);
            return new LettuceConnectionFactory(host, port);
        }

        @Bean
        RedisCacheManager cacheManager(final RedisConnectionFactory redisConnectionFactory,
            final Map<String, RedisCacheConfiguration> cacheConfigurationsByCacheName) {

            return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(createCacheConfiguration(DEFAULT_TTL, COMPRESSION_THRESHOLD_BYTES))
                .withInitialCacheConfigurations(cacheConfigurationsByCacheName).build();
        }

        @Bean
        Map<String, RedisCacheConfiguration> redisCacheConfigurationByCacheName(
            final RedisConfigurationProperties properties) {

            Map<String, RedisCacheConfiguration> redisCacheConfigurationsByName = new HashMap<>();

            properties.configsByName().forEach((name, config) -> {
                redisCacheConfigurationsByName.put(name,
                    createCacheConfiguration(config.ttl(), config.compressionThresholdBytes()));
            });

            return redisCacheConfigurationsByName;
        }

        RedisCacheConfiguration createCacheConfiguration(final Duration ttl,
            final int compressionThresholdBytes) {

            RedisSerializer<String> keySerializer = new StringRedisSerializer();
            RedisSerializer<?> valueSerializer = new RedisCompressionSerializer<>(
                new GenericJackson2JsonRedisSerializer(), compressionThresholdBytes);

            return RedisCacheConfiguration.defaultCacheConfig().entryTtl(ttl).serializeKeysWith(
                    RedisSerializationContext.SerializationPair.fromSerializer(keySerializer))
                .serializeValuesWith(
                    RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer));
        }

        @Bean
        RedisConfigurationProperties properties() {
            Map<String, CacheConfig> configsByName = new HashMap<>();
            CacheConfig testConfig = new CacheConfig(100, Duration.ofHours(1));
            configsByName.put(DEFAULT_CACHE_NAME, testConfig);

            return new RedisConfigurationProperties(
                REDIS_CONTAINER.getHost(), REDIS_CONTAINER.getMappedPort(REDIS_PORT),
                "password", Duration.ofSeconds(5), configsByName);
        }

        @Bean
        StringRedisTemplate stringRedisTemplate(
            final RedisConnectionFactory redisConnectionFactory) {
            return new StringRedisTemplate(redisConnectionFactory);
        }
    }
}
