package com.beautify_project.bp_mysql.entity.adapter;

import com.beautify_project.bp_mysql.entity.ShopLike;
import java.io.Serializable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class ShopLikeAdapter {

    private ShopLikeIdAdapter id;
    private Long registeredTime;

    private ShopLikeAdapter(final ShopLikeIdAdapter idAdapter, final Long registeredTime) {
        this.id = idAdapter;
        this.registeredTime = registeredTime;
    }

    public static ShopLike toEntity(final ShopLikeAdapter adapter) {
        return ShopLike.of(adapter.id.getShopId(), adapter.id.getMemberEmail());
    }

    public static ShopLikeAdapter toAdapter(final ShopLike entity) {
        return of(entity.getId().getShopId(), entity.getId().getMemberEmail(),
            System.currentTimeMillis());
    }

    public static ShopLikeAdapter of(final Long shopId, final String memberEmail, long registeredTime) {
        return new ShopLikeAdapter(ShopLikeIdAdapter.of(shopId, memberEmail), registeredTime);
    }


    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ShopLikeIdAdapter implements Serializable {
        private Long shopId;
        private String memberEmail;

        private ShopLikeIdAdapter(final Long shopId, final String memberEmail) {
            this.shopId = shopId;
            this.memberEmail = memberEmail;
        }

        public static ShopLikeIdAdapter of(final Long shopId, final String memberEmail) {
            return new ShopLikeIdAdapter(shopId, memberEmail);
        }
    }

}
