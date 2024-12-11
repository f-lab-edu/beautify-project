package com.beautify_project.bp_app_api.entity;

import com.beautify_project.bp_app_api.utils.UUIDGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "shop_category")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShopCategory {

    @Id
    @Column(name = "shop_category_id")
    private String id;

    @Column(name = "shop_id")
    private String shopId;

    @Column(name = "category_id")
    private String categoryId;

    @Column(name = "shop_category_registered_time")
    private Long registeredTime;

    private ShopCategory(final String id, final String shopId, final String categoryId,
        final Long registeredTime) {
        this.id = id;
        this.shopId = shopId;
        this.categoryId = categoryId;
        this.registeredTime = registeredTime;
    }

    public static ShopCategory of(final String shopId, final String categoryId) {
        return new ShopCategory(UUIDGenerator.generate(), shopId, categoryId,
            System.currentTimeMillis());
    }
}
