package com.beautify_project.bp_common_kafka.event;

import com.beautify_project.bp_utils.Validator;

public record ShopLikeEvent (Long shopId, String memberEmail, LikeType type){

    public ShopLikeEvent(final Long shopId, final String memberEmail, final LikeType type) {
        this.shopId = shopId;
        this.memberEmail = memberEmail;
        this.type = type;
        validate(shopId, memberEmail, type);
    }

    private void validate(final Long shopId, final String memberEmail, final LikeType type) {
        if (shopId == null) {
            throw new IllegalArgumentException("shopId 파라미터는 필수값 입니다.");
        }
        if (Validator.isEmptyOrBlank(memberEmail)) {
            throw new IllegalArgumentException("memberEmail 파라미터는 필수값 입니다.");
        }
        if (type == null) {
            throw new IllegalArgumentException("좋아요 유형 값은 필수값 입니다.");
        }
    }

    public enum LikeType {
        LIKE, LIKE_CANCEL
    }
}
