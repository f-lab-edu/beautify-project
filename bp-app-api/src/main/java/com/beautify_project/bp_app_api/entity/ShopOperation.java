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
@Table(name = "shop_operation")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShopOperation {

    @Id
    @Column(name = "shop_operation_id")
    private String id;

    @Column(name = "shop_id")
    private String shopId;

    @Column(name = "operation_id")
    private String operationId;

    @Column(name = "shop_operation_registered_time")
    private Long registeredTime;

    public ShopOperation(final String id, final String shopId, final String operationId,
        final Long registeredTime) {
        this.id = id;
        this.shopId = shopId;
        this.operationId = operationId;
        this.registeredTime = registeredTime;
    }

    public static ShopOperation of(final String shopId, final String operationId) {
        return new ShopOperation(UUIDGenerator.generate(), shopId, operationId,
            System.currentTimeMillis());
    }
}
