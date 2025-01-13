package com.beautify_project.bp_app_api.enumeration;

import com.beautify_project.bp_app_api.exception.BpCustomException;
import com.beautify_project.bp_app_api.dto.ErrorResponseMessage.ErrorCode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
public enum ShopSearchType {
    SHOP_NAME("name"),
    LOCATION("location"),
    LIKE("likes"),
    RATE("rate")
    ;

    private final String entityName;

    ShopSearchType(final String entityName) {
        this.entityName = entityName;
    }

    public static ShopSearchType from(final String input) {
        String inputUpperCase = input.toUpperCase();

        if (StringUtils.equals("SHOPNAME", inputUpperCase)) {
            inputUpperCase = "SHOP_NAME";
        }

        try {
            return valueOf(inputUpperCase);
        } catch (IllegalArgumentException e) {
            throw new BpCustomException("ShopSearchType 에 해당하는 값 '" + input + "' 은 올바르지 않습니다.",
                ErrorCode.BR001);
        }
    }
}
