package com.bp.app.api.dto;

import java.text.SimpleDateFormat;
import java.util.Date;

public record AccessTokenDto(String grantType, String accessToken, long accessTokenExpiresIn,
                             String refreshToken, long refreshTokenExpiresIn) {

    @Override
    public String toString() {

        final SimpleDateFormat formatter = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        String time = formatter.format(new Date(accessTokenExpiresIn));

        return "AccessTokenDto{" +
            "grantType='" + grantType + '\'' +
            ", accessToken='" + accessToken + '\'' +
            ", accessTokenExpiresIn=" + formatter.format(new Date(accessTokenExpiresIn)) +
            ", refreshToken='" + refreshToken + '\'' +
            ", refreshTokenExpiresIn=" + formatter.format(new Date(refreshTokenExpiresIn)) +
            '}';
    }
}

