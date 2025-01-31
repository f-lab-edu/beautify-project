package com.bp.app.api.request.auth;

import jakarta.validation.constraints.NotBlank;

public record EmailDuplicatedRequest(@NotBlank String email) {

}
