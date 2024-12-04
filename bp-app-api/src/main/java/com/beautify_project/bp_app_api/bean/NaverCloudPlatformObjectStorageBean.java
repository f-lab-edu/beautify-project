package com.beautify_project.bp_app_api.bean;

import com.beautify_project.bp_app_api.config.NaverCloudPlatformObjectStorageConfig;
import com.beautify_project.bp_s3_client.naver.NCPConfig;
import com.beautify_project.bp_s3_client.naver.NCPObjectStorageClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class NaverCloudPlatformObjectStorageBean {

    private final NaverCloudPlatformObjectStorageConfig naverCloudPlatformObjectStorageConfig;

    @Bean
    NCPObjectStorageClient ncpObjectStorageClient() {
      log.debug("{}", naverCloudPlatformObjectStorageConfig.toString());
        return new NCPObjectStorageClient(new NCPConfig(naverCloudPlatformObjectStorageConfig.endpoint(),
            naverCloudPlatformObjectStorageConfig.regionName(), naverCloudPlatformObjectStorageConfig.bucketName(),
            naverCloudPlatformObjectStorageConfig.accessKey(), naverCloudPlatformObjectStorageConfig.secretKey()));
    }

}
