package com.bp.domain.mysql.enums;


import com.bp.domain.mysql.exception.EnumMismatchException;

public enum OrderType {
    ASC,
    DESC
    ;

    public static OrderType from(final String input) {
        final String inputUpperCase = input.toUpperCase();
        try {
            return valueOf(inputUpperCase);
        } catch (IllegalArgumentException e) {
            throw new EnumMismatchException("OrderType", input);
        }
    }
}
