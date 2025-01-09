package com.beautify_project.bp_app_api.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Slf4j
@Configuration
@EnableAsync
public class IOBoundAsyncThreadPoolConfiguration implements AsyncConfigurer{

    private static final int CORE_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int IO_THREAD_POOL_SIZE = CORE_COUNT * 2;
    private static final int IO_THREAD_POOL_MAX_SIZE = CORE_COUNT * 4;
    private static final int IO_THREAD_POOL_QUEUE_CAPACITY = 100;
    private static final int IO_THREAD_KEEP_ALIVE_SECONDS = 60;
    private static final int IO_THREAD_AWAIT_TERMINATION_SECONDS = 60;
    private static final String IO_THREAD_NAME_PREFIX = "IO-Async-Executor";

    @Bean("ioBoundExecutor")
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(IO_THREAD_POOL_SIZE);
        executor.setMaxPoolSize(IO_THREAD_POOL_MAX_SIZE);
        executor.setQueueCapacity(IO_THREAD_POOL_QUEUE_CAPACITY);
        executor.setKeepAliveSeconds(IO_THREAD_KEEP_ALIVE_SECONDS);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(IO_THREAD_AWAIT_TERMINATION_SECONDS);
        executor.setThreadNamePrefix(IO_THREAD_NAME_PREFIX);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy()); // thread 실행 불가능할 때 호출한 스레드에서 실행
        logging();
        return executor;
    }

    private void logging() {
        log.debug("CORE_COUNT: {}", CORE_COUNT);
        log.debug("IO_THREAD_POOL_SIZE: {}", IO_THREAD_POOL_SIZE);
        log.debug("IO_THREAD_POOL_MAX_SIZE: {}", IO_THREAD_POOL_MAX_SIZE);
        log.debug("IO_THREAD_POOL_QUEUE_CAPACITY: {}", IO_THREAD_POOL_QUEUE_CAPACITY);
        log.debug("IO_THREAD_KEEP_ALIVE_SECONDS: {}", IO_THREAD_KEEP_ALIVE_SECONDS);
    }

}
