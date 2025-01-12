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
@Table(name = "shop_operation")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShopOperation {

    @EmbeddedId
    private ShopOperationId id;

    @Column(name = "shop_operation_registered_time")
    private Long registeredTime;

    private ShopOperation(final ShopOperationId id, final Long registeredTime) {
        this.id = id;
        this.registeredTime = registeredTime;
    }

    public static ShopOperation of(final String shopId, final String operationId) {
        return new ShopOperation(ShopOperationId.of(shopId, operationId),
            System.currentTimeMillis());
    }

    @Embeddable
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ShopOperationId implements Serializable {

        @Column(name = "shop_id")
        private String shopId;

        @Column(name = "operation_id")
        private String operationId;

        private ShopOperationId(final String shopId, final String operationId) {
            this.shopId = shopId;
            this.operationId = operationId;
        }

        public static ShopOperationId of(final String shopId, final String operationId) {
            return new ShopOperationId(shopId, operationId);
        }
    }
}
