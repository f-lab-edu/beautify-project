package com.beautify_project.bp_app_api.config;

import com.beautify_project.bp_app_api.dto.common.ErrorResponseMessage.ErrorCode;
import com.beautify_project.bp_app_api.exception.ConfigurationException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("storage")
public record StorageConfig (String type) {

    public StorageConfig {
        validate(type);
    }

    private void validate(final String type) {
        if (StringUtils.isEmpty(type)) {
            throw new ConfigurationException("storage 설정값이 올바르지 않습니다.", ErrorCode.IS001);
        }
    }
}
