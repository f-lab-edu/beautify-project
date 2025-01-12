package com.beautify_project.bp_mysql.enums;


import com.beautify_project.bp_mysql.exception.EnumMismatchException;

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
