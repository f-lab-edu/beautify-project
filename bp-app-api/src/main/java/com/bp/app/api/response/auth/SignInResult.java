package com.bp.app.api.response.auth;

public record SignInResult(String accessToken, Long accessTokenExpirationTime, String refreshToken,
                           Long refreshTokenExpirationTime) {


}
