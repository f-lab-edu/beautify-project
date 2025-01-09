package com.beautify_project.bp_app_api.enumeration;

import com.beautify_project.bp_app_api.exception.EnumMismatchException;

public enum Role {
    USER, ADMIN, OWNER;

    private static final String PREFIX_FOR_SECURITY_FILTER_CHAIN = "ROLE_";

    public static Role from(final String input) {
        try {
            return valueOf(input.toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new EnumMismatchException(Role.class.getName(), input);
        }
    }

    public String nameForSecurityFilterChain() {
        return PREFIX_FOR_SECURITY_FILTER_CHAIN + this.name();
    }
}
