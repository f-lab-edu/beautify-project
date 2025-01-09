package com.beautify_project.bp_app_api.enumeration;

import com.beautify_project.bp_app_api.exception.EnumMismatchException;

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
