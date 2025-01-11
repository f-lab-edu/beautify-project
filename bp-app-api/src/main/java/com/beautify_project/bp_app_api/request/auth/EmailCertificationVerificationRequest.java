package com.beautify_project.bp_app_api.request.auth;

import jakarta.validation.constraints.NotBlank;

public record EmailCertificationVerificationRequest(@NotBlank String email,
                                                    @NotBlank String certificationNumber) {


}
