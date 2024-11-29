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
    private Long registered;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Shop shop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operation_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Operation operation;

    private ShopOperation(final Long registered, final Shop shop, final Operation operation) {
        this.id = UUIDGenerator.generate();
        this.registered = registered;
        this.shop = shop;
        this.operation = operation;
    }

    public static ShopOperation of(final Shop shop, final Operation operation,
        final Long registered) {
        return new ShopOperation(registered, shop, operation);
    }
}
