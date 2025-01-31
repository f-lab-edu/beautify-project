package com.bp.app.api.request.auth;

import jakarta.validation.constraints.NotBlank;

public record EmailCertificationVerificationRequest(@NotBlank String email,
                                                    @NotBlank String certificationNumber) {


}
