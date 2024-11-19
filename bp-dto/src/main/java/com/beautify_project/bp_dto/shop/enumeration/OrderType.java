package com.beautify_project.bp_dto.shop.enumeration;

import com.beautify_project.bp_dto.exception.EnumMismatchException;

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
