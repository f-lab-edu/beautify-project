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
@Table(name = "shop_facility")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShopFacility extends BaseEntity {

    @EmbeddedId
    private ShopFacilityId id;

    private ShopFacility(final ShopFacilityId id) {
        this.id = id;
    }

    public static ShopFacility newShopFacility(final Long shopId, final Long facilityId) {
        return new ShopFacility(ShopFacilityId.newShopFacilityId(shopId, facilityId));
    }

    @Embeddable
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ShopFacilityId implements Serializable {

        @Column(name = "shop_id")
        private Long shopId;

        @Column(name = "facility_id")
        private Long facilityId;

        private ShopFacilityId(final Long shopId, final Long facilityId) {
            this.shopId = shopId;
            this.facilityId = facilityId;
        }

        public static ShopFacilityId newShopFacilityId(final Long shopId, final Long facilityId) {
            return new ShopFacilityId(shopId, facilityId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(shopId, facilityId);
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }

            final ShopFacilityId that = (ShopFacilityId) obj;
            return Objects.equals(shopId, that.shopId) && Objects.equals(facilityId,
                that.facilityId);
        }
    }
}
