package com.beautify_project.bp_app_api.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record EmailCertificationRequest(@NotBlank String email) {

}
