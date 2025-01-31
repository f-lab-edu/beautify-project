package com.bp.domain.mysql.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "shop_like")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShopLike extends BaseEntity {

    @EmbeddedId
    private ShopLikeId id;

    private ShopLike(final ShopLikeId id) {
        this.id = id;
    }

    public static ShopLike newShopLike(final Long shopId, final String memberEmail) {
        return new ShopLike(ShopLikeId.newShopLikeId(shopId, memberEmail));
    }

    public boolean isEmpty() {
        return this.id.memberEmail == null || this.id.shopId == null;
    }

    @Embeddable
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ShopLikeId implements Serializable {

        @Column(name = "shop_id")
        private Long shopId;

        @Column(name = "member_email")
        private String memberEmail;

        private ShopLikeId(final Long shopId, final String memberEmail) {
            this.shopId = shopId;
            this.memberEmail = memberEmail;
        }

        public static ShopLikeId newShopLikeId(final Long shopId, final String memberEmail) {
            return new ShopLikeId(shopId, memberEmail);
        }

        @Override
        public int hashCode() {
            return Objects.hash(shopId, memberEmail);
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }

            final ShopLikeId that = (ShopLikeId) obj;
            return Objects.equals(shopId, that.shopId) && Objects.equals(memberEmail, that.memberEmail);
        }
    }
}
