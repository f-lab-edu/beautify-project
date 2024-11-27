package com.beautify_project.bp_app_api.enumeration;

import com.beautify_project.bp_app_api.exception.EnumMismatchException;
import org.apache.commons.lang3.StringUtils;

public enum ReviewSortBy {
    REGISTERED_DATE,
    RATE
    ;

    public static ReviewSortBy from(final String input) {
        String inputUpperCase = input.toUpperCase();
        try {
            if (StringUtils.equals("REGISTEREDDATE", inputUpperCase)) {
                inputUpperCase = "REGISTERED_DATE";
            }
            return valueOf(inputUpperCase);
        } catch (IllegalArgumentException e) {
            throw new EnumMismatchException("ReviewOrderBy", input);
        }
    }
}
