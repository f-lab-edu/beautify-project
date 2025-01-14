package com.beuatify_project.bp_common.event;

import com.beautify_project.bp_utils.Validator;

public record ShopLikeCancelEvent(Long shopId, String memberEmail) {

    public ShopLikeCancelEvent(final Long shopId, final String memberEmail) {
        validate(shopId, memberEmail);
        this.shopId = shopId;
        this.memberEmail = memberEmail;
    }

    private void validate(final Long shopId, final String memberEmail) {
        if (shopId == null) {
            throw new UnsupportedOperationException("shopId 파라미터는 필수값 입니다.");
        }
        if (Validator.isEmptyOrBlank(memberEmail)) {
            throw new UnsupportedOperationException("memberEmail 파라미터는 필수값 입니다.");
        }
    }

}
