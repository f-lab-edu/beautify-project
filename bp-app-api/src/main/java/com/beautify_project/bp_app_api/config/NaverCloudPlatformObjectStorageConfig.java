package com.beautify_project.bp_app_api.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "ncp")
public record NaverCloudPlatformObjectStorageConfig(String endpoint, String regionName, String bucketName, String accessKey,
                                                    String secretKey) {

    public NaverCloudPlatformObjectStorageConfig {
        validate(endpoint, regionName, bucketName, accessKey, secretKey);
    }

    // TODO: jakarta.validation 으로 개선필요 (정상 동작하지 않기 때문에 임시 조치함)
    private void validate(String endpoint, String regionName, String bucketName, String accessKey,
        String secretKey) {
        if (StringUtils.isEmpty(endpoint) || StringUtils.isEmpty(regionName) ||
            StringUtils.isEmpty(bucketName) || StringUtils.isEmpty(accessKey)
            || StringUtils.isEmpty(secretKey)) {
            throw new IllegalStateException("naver cloud platform 관련 설정값이 올바르지 않습니다.");
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
