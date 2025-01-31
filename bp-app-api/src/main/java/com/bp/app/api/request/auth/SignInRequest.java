package com.bp.app.api.request.auth;

import jakarta.validation.constraints.NotBlank;

public record SignInRequest(@NotBlank String email, @NotBlank String password) {
}
