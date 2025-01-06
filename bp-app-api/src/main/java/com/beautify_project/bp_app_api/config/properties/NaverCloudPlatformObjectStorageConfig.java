package com.beautify_project.bp_app_api.config.properties;

import com.beautify_project.bp_app_api.dto.common.ErrorResponseMessage.ErrorCode;
import com.beautify_project.bp_app_api.exception.ConfigurationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "ncp")
@Slf4j
public record NaverCloudPlatformObjectStorageConfig(String enabled, String endpoint, String regionName, String bucketName, String accessKey,
                                                    String secretKey) {

    public NaverCloudPlatformObjectStorageConfig {
        validate(enabled, endpoint, regionName, bucketName, accessKey, secretKey);
    }

    private void validate(String enabled, String endpoint, String regionName, String bucketName, String accessKey,
        String secretKey) {
        if (StringUtils.isEmpty(enabled) || StringUtils.equals("FALSE", enabled.toUpperCase())) {
            log.info("NaverCloudPlatformObjectStorageRepository is disabled");
            return;
        }

        if (StringUtils.isEmpty(endpoint) || StringUtils.isEmpty(regionName) ||
            StringUtils.isEmpty(bucketName) || StringUtils.isEmpty(accessKey)
            || StringUtils.isEmpty(secretKey)) {
            throw new ConfigurationException("naver cloud platform 관련 설정값이 올바르지 않습니다.",
                ErrorCode.IS001);
        }
    }

    @Override
    public String toString() {
        return "NaverCloudPlatformObjectStorageConfig{" +
            "bucketName='" + bucketName + '\'' +
            ", regionName='" + regionName + '\'' +
            ", endpoint='" + endpoint + '\'' +
            '}';
    }
}
