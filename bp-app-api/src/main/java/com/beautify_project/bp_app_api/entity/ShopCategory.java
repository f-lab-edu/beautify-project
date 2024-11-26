package com.beautify_project.bp_app_api.entity;

import com.beautify_project.bp_app_api.utils.UUIDGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "shop_category")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShopCategory {

    @Id
    @Column(name = "shop_category_id")
    private String id;

    private String categoryId;
    private String categoryName;
    private Long registered;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Shop shop;

    @Builder
    protected ShopCategory(final Shop shop, final String categoryId, final String categoryName,
        final Long registered) {
        this.id = UUIDGenerator.generate();
        this.shop = shop;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.registered = registered;
    }
}
