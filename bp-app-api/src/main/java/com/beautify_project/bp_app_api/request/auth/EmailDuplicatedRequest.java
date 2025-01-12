package com.beautify_project.bp_app_api.request.auth;

import jakarta.validation.constraints.NotBlank;

public record EmailDuplicatedRequest(@NotBlank String email) {

}
