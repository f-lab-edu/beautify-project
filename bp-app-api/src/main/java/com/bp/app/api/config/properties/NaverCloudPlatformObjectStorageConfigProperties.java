package com.bp.app.api.config.properties;

import com.bp.app.api.exception.BpCustomException;
import com.bp.app.api.response.ErrorResponseMessage.ErrorCode;
import com.bp.utils.Validator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "ncp")
@Slf4j
public record NaverCloudPlatformObjectStorageConfigProperties(String enabled, String endpoint, String regionName, String bucketName, String accessKey,
                                                              String secretKey) {

    public NaverCloudPlatformObjectStorageConfigProperties {
        validate(enabled, endpoint, regionName, bucketName, accessKey, secretKey);
    }

    private void validate(String enabled, String endpoint, String regionName, String bucketName, String accessKey,
        String secretKey) {

        if (Validator.isEmptyOrBlank(enabled) || StringUtils.equals("FALSE",
            enabled.toUpperCase())) {
            log.info("NaverCloudPlatformObjectStorageRepository is disabled");
            return;
        }

        if (Validator.isEmptyOrBlank(endpoint) || Validator.isEmptyOrBlank(regionName)
            || Validator.isEmptyOrBlank(bucketName) || Validator.isEmptyOrBlank(accessKey)) {
            throw new BpCustomException("Naver cloud paltform 관련 설정값이 올바르지 않습니다.", ErrorCode.IS001);
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
