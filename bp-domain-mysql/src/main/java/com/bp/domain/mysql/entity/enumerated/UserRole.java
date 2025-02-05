package com.bp.domain.mysql.entity.enumerated;

import com.bp.domain.mysql.exception.EnumMismatchException;

public enum UserRole {
    USER, ADMIN, OWNER, OPERATOR;

    private static final String PREFIX_FOR_SECURITY_FILTER_CHAIN = "ROLE_";

    public static UserRole from(final String input) {
        try {
            return valueOf(input.toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new EnumMismatchException(UserRole.class.getName(), input);
        }
    }

    public String nameForSecurityFilterChain() {
        return PREFIX_FOR_SECURITY_FILTER_CHAIN + this.name();
    }
}
