package com.bp.app.api.config;

import com.bp.app.api.config.properties.AsyncThreadPoolConfigurationProperties;
import com.bp.utils.Validator;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Slf4j
@Configuration
@EnableAsync
@RequiredArgsConstructor
public class IOBoundAsyncThreadPoolConfiguration implements AsyncConfigurer {

    private static final int CORE_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int DEFAULT_IO_THREAD_POOL_SIZE = CORE_COUNT * 2;
    private static final int DEFAULT_IO_THREAD_POOL_MAX_SIZE = CORE_COUNT * 4;
    private static final int DEFAULT_IO_THREAD_POOL_QUEUE_CAPACITY = 100;
    private static final int DEFAULT_IO_THREAD_KEEP_ALIVE_SECONDS = 60;
    private static final int DEFAULT_IO_THREAD_AWAIT_TERMINATION_SECONDS = 60;
    private static final String DEFAULT_IO_THREAD_NAME_PREFIX = "IO-Async-Executor";

    private final AsyncThreadPoolConfigurationProperties properties;

    @Bean("ioBoundExecutor")
    @Override
    public Executor getAsyncExecutor() {

        int corePoolSize = DEFAULT_IO_THREAD_POOL_SIZE;
        if (!Validator.isNullOrZero(properties.corePoolSize())) {
            corePoolSize = properties.corePoolSize();
        }

        int maxPoolSize = DEFAULT_IO_THREAD_POOL_MAX_SIZE;
        if (!Validator.isNullOrZero(properties.maxPoolSize())) {
            maxPoolSize = properties.maxPoolSize();
        }

        int queueCapacity = DEFAULT_IO_THREAD_POOL_QUEUE_CAPACITY;
        if (!Validator.isNullOrZero(properties.queueCapacity())) {
            queueCapacity = properties.queueCapacity();
        }

        String threadNamePrefix = DEFAULT_IO_THREAD_NAME_PREFIX;
        if (!Validator.isEmptyOrBlank(properties.threadNamePrefix())) {
            threadNamePrefix = properties.threadNamePrefix();
        }

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setKeepAliveSeconds(DEFAULT_IO_THREAD_KEEP_ALIVE_SECONDS);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(DEFAULT_IO_THREAD_AWAIT_TERMINATION_SECONDS);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy()); // thread 실행 불가능할 때 호출한 스레드에서 실행
        logging();
        return executor;
    }

    private static void logging() {
        log.debug("CORE_COUNT: {}", CORE_COUNT);
        log.debug("IO_THREAD_POOL_SIZE: {}", DEFAULT_IO_THREAD_POOL_SIZE);
        log.debug("IO_THREAD_POOL_MAX_SIZE: {}", DEFAULT_IO_THREAD_POOL_MAX_SIZE);
        log.debug("IO_THREAD_POOL_QUEUE_CAPACITY: {}", DEFAULT_IO_THREAD_POOL_QUEUE_CAPACITY);
        log.debug("IO_THREAD_KEEP_ALIVE_SECONDS: {}", DEFAULT_IO_THREAD_KEEP_ALIVE_SECONDS);
    }
}
