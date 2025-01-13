package com.beautify_project.bp_app_api.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record SignInRequest(@NotBlank String email, @NotBlank String password) {

}
