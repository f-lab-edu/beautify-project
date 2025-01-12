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
@Table(name = "shop_facility")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShopFacility {

    @EmbeddedId
    private ShopFacilityId id;

    @Column(name = "shop_facility_registered_time")
    private Long registeredTime;

    private ShopFacility(final ShopFacilityId id, final Long registeredTime) {
        this.id = id;
        this.registeredTime = registeredTime;
    }

    public static ShopFacility of(final String shopId, final String facilityId) {
        return new ShopFacility(ShopFacilityId.of(shopId, facilityId), System.currentTimeMillis());
    }

    @Embeddable
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ShopFacilityId implements Serializable {

        @Column(name = "shop_id")
        private String shopId;

        @Column(name = "facility_id")
        private String facilityId;

        private ShopFacilityId(final String shopId, final String facilityId) {
            this.shopId = shopId;
            this.facilityId = facilityId;
        }

        public static ShopFacilityId of(final String shopId, final String facilityId) {
            return new ShopFacilityId(shopId, facilityId);
        }
    }
}
