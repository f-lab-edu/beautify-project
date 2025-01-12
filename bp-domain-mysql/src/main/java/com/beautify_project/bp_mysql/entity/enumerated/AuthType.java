package com.beautify_project.bp_mysql.entity.enumerated;

import com.beautify_project.bp_mysql.exception.EnumMismatchException;

public enum AuthType {
    NAVER, KAKAO, BP;

    public static AuthType from(final String input) {
        try {
            return valueOf(input.toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new EnumMismatchException(AuthType.class.getName(), input);
        }
    }
}
