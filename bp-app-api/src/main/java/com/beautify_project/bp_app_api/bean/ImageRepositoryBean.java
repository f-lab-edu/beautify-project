package com.beautify_project.bp_app_api.bean;


import com.beautify_project.bp_app_api.config.NaverCloudPlatformObjectStorageConfig;
import com.beautify_project.bp_app_api.config.StorageConfig;
import com.beautify_project.bp_app_api.dto.common.ErrorResponseMessage.ErrorCode;
import com.beautify_project.bp_app_api.exception.ConfigurationException;
import com.beautify_project.bp_app_api.repository.image.ImageRepository;
import com.beautify_project.bp_app_api.repository.image.NaverCloudPlatformObjectStorageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ImageRepositoryBean {

    private static final String STORAGE_TYPE_NAVER_CLOUD_PLATFORM_OBJECT_STORAGE = "NAVER-CLOUD-PLATFORM-OBJECT-STORAGE";
    private final StorageConfig storageConfig;
    private final NaverCloudPlatformObjectStorageConfig ncpObjectStorageConfig;

    @Bean
    ImageRepository imageRepository() {
        final String storageType = storageConfig.type().toUpperCase();
        if (StringUtils.equals(STORAGE_TYPE_NAVER_CLOUD_PLATFORM_OBJECT_STORAGE, storageType)) {
            return new NaverCloudPlatformObjectStorageRepository(ncpObjectStorageConfig);
        }

        throw new ConfigurationException("설정값이 맞지 않아 등록할 repository 가 없습니다", ErrorCode.IS001);
    }
}
