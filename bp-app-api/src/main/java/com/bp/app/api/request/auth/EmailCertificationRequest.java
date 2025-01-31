package com.bp.app.api.request.auth;

import jakarta.validation.constraints.NotBlank;

public record EmailCertificationRequest(@NotBlank String email) {

}
