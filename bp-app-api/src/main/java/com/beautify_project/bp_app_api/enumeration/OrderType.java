package com.beautify_project.bp_app_api.enumeration;


import com.beautify_project.bp_app_api.exception.EnumMismatchException;

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
