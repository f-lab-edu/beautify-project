package com.beautify_project.bp_app_api.dto.auth;

public record SignInResult(String accessToken, Long accessTokenExpirationTime, String refreshToken, Long refreshTokenExpirationTime) {
}
