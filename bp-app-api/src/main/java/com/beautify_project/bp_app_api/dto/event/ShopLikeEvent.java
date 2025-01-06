package com.beautify_project.bp_app_api.dto.event;

import com.beautify_project.bp_app_api.utils.Validator;

public record ShopLikeEvent (String shopId, String memberEmail){

    public ShopLikeEvent(final String shopId, final String memberEmail) {
        validate(shopId, memberEmail);
        this.shopId = shopId;
        this.memberEmail = memberEmail;
    }

    private void validate(final String shopId, final String memberEmail) {

        if (Validator.isEmptyOrBlank(shopId)) {
            throw new UnsupportedOperationException("shopId 파라미터는 필수값 입니다.");
        }

        if (Validator.isEmptyOrBlank(memberEmail)) {
            throw new UnsupportedOperationException("memberEmail 파라미터는 필수값 입니다.");
        }
    }
}
