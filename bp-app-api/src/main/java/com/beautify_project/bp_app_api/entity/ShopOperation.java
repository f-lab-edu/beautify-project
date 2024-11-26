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
@Table(name = "shop_operation")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShopOperation {

    @Id
    @Column(name = "shop_operation_id")
    private String id;

    private String operationId;
    private String operationName;
    private Long registered;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Shop shop;

    @Builder
    protected ShopOperation(final String operationId, final String operationName,
        final Long registered, final Shop shop) {
        this.id = UUIDGenerator.generate();
        this.operationId = operationId;
        this.operationName = operationName;
        this.registered = registered;
        this.shop = shop;
    }
}
