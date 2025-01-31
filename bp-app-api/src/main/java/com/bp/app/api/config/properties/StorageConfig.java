package com.bp.app.api.config.properties;

import com.bp.app.api.exception.BpCustomException;
import com.bp.app.api.response.ErrorResponseMessage.ErrorCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("storage")
public record StorageConfig (String type) {

    public StorageConfig {
        validate(type);
    }

    private void validate(final String type) {
        if (StringUtils.isEmpty(type)) {
            throw new BpCustomException("storage 설정값이 올바르지 않습니다.", ErrorCode.IS001);
        }
    }
}
