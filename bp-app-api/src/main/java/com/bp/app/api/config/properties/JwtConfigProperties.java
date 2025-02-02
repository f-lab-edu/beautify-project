package com.bp.app.api.config.properties;

import com.bp.utils.Validator;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("jwt")
public record JwtConfigProperties(String secretKey, long accessTokenExpiredMinute,
                                  long refreshTokenExpiredMinute) {

    public JwtConfigProperties(final String secretKey, final long accessTokenExpiredMinute,
        final long refreshTokenExpiredMinute) {
        this.secretKey = secretKey;
        this.accessTokenExpiredMinute = accessTokenExpiredMinute;
        this.refreshTokenExpiredMinute = refreshTokenExpiredMinute;
        validate();
    }

    private void validate() {
        if (Validator.isEmptyOrBlank(secretKey) || accessTokenExpiredMinute == 0
            || refreshTokenExpiredMinute == 0) {
            throw new IllegalStateException("JWT 관련 설정값이 유효하지 않습니다.");
        }
    }
}

