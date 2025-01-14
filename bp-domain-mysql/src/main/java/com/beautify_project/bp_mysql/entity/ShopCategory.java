package com.beautify_project.bp_mysql.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.io.Serializable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "shop_category")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShopCategory {

    @EmbeddedId
    private ShopCategoryId id;

    @Column(name = "shop_category_registered_time")
    private Long registeredTime;

    public ShopCategory(final ShopCategoryId id, final Long registeredTime) {
        this.id = id;
        this.registeredTime = registeredTime;
    }

    public static ShopCategory of(final Long shopId, final String categoryId) {
        return new ShopCategory(ShopCategoryId.of(shopId, categoryId), System.currentTimeMillis());
    }

    @Embeddable
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ShopCategoryId implements Serializable {

        @Column(name = "shop_id")
        private Long shopId;

        @Column(name = "category_id")
        private String categoryId;

        private ShopCategoryId(final Long shopId, final String categoryId) {
            this.shopId = shopId;
            this.categoryId = categoryId;
        }

        public static ShopCategoryId of(final Long shopId, final String categoryId) {
            return new ShopCategoryId(shopId, categoryId);
        }
    }
}
