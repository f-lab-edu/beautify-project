package com.bp.app.api.request.member;

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
