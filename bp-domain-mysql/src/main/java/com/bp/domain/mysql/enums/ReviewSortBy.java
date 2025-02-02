package com.bp.domain.mysql.enums;


import com.bp.domain.mysql.exception.EnumMismatchException;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
public enum ReviewSortBy {
    CREATED_DATE("createdDate"),
    RATE("rate")
    ;

    private final String value;

    ReviewSortBy(final String value) {
        this.value = value;
    }

    public static ReviewSortBy from(final String input) {
        String inputUpperCase = input.toUpperCase();
        try {
            if (StringUtils.equals("CREATEDDATE", inputUpperCase)) {
                inputUpperCase = "CREATED_DATE";
            }
            return valueOf(inputUpperCase);
        } catch (IllegalArgumentException e) {
            throw new EnumMismatchException("ReviewOrderBy", input);
        }
    }
}
