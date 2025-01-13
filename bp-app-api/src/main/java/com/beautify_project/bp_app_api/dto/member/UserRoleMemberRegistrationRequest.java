package com.beautify_project.bp_app_api.dto.member;

import jakarta.validation.constraints.NotBlank;

public record UserRoleMemberRegistrationRequest(
    @NotBlank
    String email,

    @NotBlank
    String password,

    @NotBlank
    String name,

    String contact) {
}
