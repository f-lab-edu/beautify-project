package com.beautify_project.bp_app_api.enumeration;

import com.beautify_project.bp_app_api.exception.EnumMismatchException;
import org.apache.commons.lang3.StringUtils;

public enum ShopSearchType {
    SHOP_NAME,
    LOCATION,
    LIKE,
    RATE
    ;

    public static ShopSearchType from(final String input) {
        String inputUpperCase = input.toUpperCase();

        if (StringUtils.equals("SHOPNAME", inputUpperCase)) {
            inputUpperCase = "SHOP_NAME";
        }

        try {
            return valueOf(inputUpperCase);
        } catch (IllegalArgumentException e) {
            throw new EnumMismatchException("ShopSearchType", input);
        }
    }
}
