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
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "shop_facility")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public class ShopFacility {
    @Id
    @Column(name = "shop_facility_id")
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Shop shop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Facility facility;

    private Long registered;

    @Builder
    public ShopFacility(final Shop shop, final Facility facility, final Long registered) {
        this.id = UUIDGenerator.generate();
        this.shop = shop;
        this.facility = facility;
        this.registered = registered;
    }
}
