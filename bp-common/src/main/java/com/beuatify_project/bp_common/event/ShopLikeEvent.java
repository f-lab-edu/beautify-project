package com.beuatify_project.bp_common.event;


import com.beautify_project.bp_utils.Validator;

public record ShopLikeEvent (Long shopId, String memberEmail){

    public ShopLikeEvent(final Long shopId, final String memberEmail) {
        validate(shopId, memberEmail);
        this.shopId = shopId;
        this.memberEmail = memberEmail;
    }

    private void validate(final Long shopId, final String memberEmail) {
        if (Validator.isEmptyOrBlank(memberEmail)) {
            throw new UnsupportedOperationException("memberEmail 파라미터는 필수값 입니다.");
        }
    }
}
