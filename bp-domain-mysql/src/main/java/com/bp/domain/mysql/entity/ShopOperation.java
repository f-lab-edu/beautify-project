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
@Table(name = "shop_operation")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShopOperation extends BaseEntity {

    @EmbeddedId
    private ShopOperationId id;

    private ShopOperation(final ShopOperationId id) {
        this.id = id;
    }

    public static ShopOperation newShopOperation(final Long shopId, final Long operationId) {
        return new ShopOperation(ShopOperationId.newShopOperationId(shopId, operationId));
    }

    @Embeddable
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ShopOperationId implements Serializable {

        @Column(name = "shop_id")
        private Long shopId;

        @Column(name = "operation_id")
        private Long operationId;

        private ShopOperationId(final Long shopId, final Long operationId) {
            this.shopId = shopId;
            this.operationId = operationId;
        }

        public static ShopOperationId newShopOperationId(final Long shopId, final Long operationId) {
            return new ShopOperationId(shopId, operationId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(shopId, operationId);
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }

            final ShopOperationId that = (ShopOperationId) obj;
            return Objects.equals(shopId, that.shopId) && Objects.equals(operationId,
                that.operationId);
        }
    }
}
