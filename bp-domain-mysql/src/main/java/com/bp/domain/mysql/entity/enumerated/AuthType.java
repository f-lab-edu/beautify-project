package com.bp.domain.mysql.entity.enumerated;

import com.bp.domain.mysql.exception.EnumMismatchException;

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
