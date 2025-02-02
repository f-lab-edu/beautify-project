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
@Table(name = "shop_category")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShopCategory extends BaseEntity{

    @EmbeddedId
    private ShopCategoryId id;

    private ShopCategory(final ShopCategoryId id) {
        this.id = id;
    }

    public static ShopCategory newShopCategory(final Long shopId, final Long categoryId) {
        return new ShopCategory(ShopCategoryId.newShopCategoryId(shopId, categoryId));
    }

    @Embeddable
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ShopCategoryId implements Serializable {

        @Column(name = "shop_id")
        private Long shopId;

        @Column(name = "category_id")
        private Long categoryId;

        private ShopCategoryId(final Long shopId, final Long categoryId) {
            this.shopId = shopId;
            this.categoryId = categoryId;
        }

        public static ShopCategoryId newShopCategoryId(final Long shopId, final Long categoryId) {
            return new ShopCategoryId(shopId, categoryId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(shopId, categoryId);
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }

            final ShopCategoryId that = (ShopCategoryId) obj;
            return Objects.equals(shopId, that.shopId) && Objects.equals(categoryId,
                that.categoryId);
        }
    }
}
