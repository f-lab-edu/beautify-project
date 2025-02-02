package com.bp.app.api.request.member;

import jakarta.validation.constraints.NotBlank;

public record OwnerRoleMemberRegistrationRequest(
    @NotBlank
    String email,

    @NotBlank
    String password,

    @NotBlank
    String name,

    @NotBlank
    String contact) {

}
