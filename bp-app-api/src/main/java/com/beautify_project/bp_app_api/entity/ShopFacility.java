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
@Table(name = "shop_facility")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShopFacility {

    @Id
    @Column(name = "shop_facility_id")
    private String id;

    @Column(name = "shop_id")
    private String shopId;

    @Column(name = "facility_id")
    private String facilityId;

    @Column(name = "shop_facility_registered_time")
    private Long registeredTime;

    public ShopFacility(final String id, final String shopId, final String facilityId,
        final Long registeredTime) {
        this.id = id;
        this.shopId = shopId;
        this.facilityId = facilityId;
        this.registeredTime = registeredTime;
    }

    public static ShopFacility of(final String shopId, final String facilityId) {
        return new ShopFacility(UUIDGenerator.generate(), shopId, facilityId,
            System.currentTimeMillis());
    }
}
