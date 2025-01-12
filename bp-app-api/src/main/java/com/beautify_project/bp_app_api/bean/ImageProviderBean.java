package com.beautify_project.bp_app_api.bean;

import com.beautify_project.bp_app_api.config.properties.NaverCloudPlatformObjectStorageConfigProperties;
import com.beautify_project.bp_app_api.config.properties.StorageConfig;
import com.beautify_project.bp_app_api.exception.BpCustomException;
import com.beautify_project.bp_app_api.response.ErrorResponseMessage.ErrorCode;
import com.beautify_project.bp_app_api.provider.image.ImageProvider;
import com.beautify_project.bp_app_api.provider.image.NCPImageProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ImageProviderBean {

    private static final String STORAGE_TYPE_NAVER_CLOUD_PLATFORM_OBJECT_STORAGE = "NAVER-CLOUD-PLATFORM-OBJECT-STORAGE";
    private final StorageConfig storageConfig;
    private final NaverCloudPlatformObjectStorageConfigProperties ncpObjectStorageConfig;

    @Bean
    ImageProvider imageProvider() {
        final String storageType = storageConfig.type().toUpperCase();
        if (StringUtils.equals(STORAGE_TYPE_NAVER_CLOUD_PLATFORM_OBJECT_STORAGE, storageType)) {
            return new NCPImageProvider(ncpObjectStorageConfig);
        }
        throw new BpCustomException("설정값이 올바르지 않아 등록할 image provider 가 없습니다.", ErrorCode.IS001);
    }
}
