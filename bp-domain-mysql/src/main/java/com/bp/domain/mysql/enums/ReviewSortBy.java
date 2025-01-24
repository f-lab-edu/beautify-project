package com.bp.domain.mysql.enums;


import com.bp.domain.mysql.exception.EnumMismatchException;
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
