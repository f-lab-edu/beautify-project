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
@Table(name = "operation_category")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OperationCategory {

    @Id
    @Column(name = "operation_category_id")
    private String id;

    private Long registered;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operation_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Operation operation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Category category;

    private OperationCategory(final Operation operation, final Category category,
        final Long registered) {
        this.id = UUIDGenerator.generate();
        this.registered = registered;
        this.operation = operation;
        this.category = category;
    }

    public static OperationCategory of(final Operation operation, final Category category,
        final Long registered) {
        return new OperationCategory(operation, category, registered);
    }
}
