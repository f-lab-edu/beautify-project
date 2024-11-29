package com.beautify_project.bp_app_api.config;

import com.beautify_project.bp_app_api.service.FileSystemStorageService;
import com.beautify_project.bp_app_api.service.S3StorageService;
import com.beautify_project.bp_app_api.service.StorageService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StorageServiceBeanConfig {

    private static final String STORAGE_IMPLEMENTATION_FILE_SYSTEM = "FILESYSTEM";
    private static final String STORAGE_IMPLEMENTATION_S3 = "S3";

    @Value("${storage.implementation:null}")
    private String storageImplementationConfig;

    @Bean
    public StorageService storageService() {
        if (StringUtils.isEmpty(storageImplementationConfig)) {
            throw new IllegalStateException("스토리지 서비스 구현체 설정값이 존재하지 않습니다.");
        }

        final String storageImplementation = storageImplementationConfig.toUpperCase();

        if (StringUtils.equals(STORAGE_IMPLEMENTATION_FILE_SYSTEM, storageImplementation)) {
            return new FileSystemStorageService();
        }

        if (StringUtils.equals(STORAGE_IMPLEMENTATION_S3, storageImplementation)) {
            return new S3StorageService();
        }

        throw new IllegalStateException("스토리지 서비스 구현체 설정값이 올바르지 않습니다.");
    }
}
