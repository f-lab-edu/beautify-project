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
@Table(name = "shop_like")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShopLike {

    @Id
    @Column(name = "shop_like_id")
    private String id;

    @Column(name = "shop_id")
    private String shopId;

    @Column(name = "member_id")
    private String memberId;

    @Column(name = "shop_like_registered_time")
    private Long registeredTime;

    private ShopLike(final String id, final String shopId, final String memberId, final Long registeredTime) {
        this.id = id;
        this.shopId = shopId;
        this.memberId = memberId;
        this.registeredTime = registeredTime;
    }

    public static ShopLike of(final String shopId, final String memberId) {
        return new ShopLike(UUIDGenerator.generate(), shopId, memberId, System.currentTimeMillis());
    }
}
