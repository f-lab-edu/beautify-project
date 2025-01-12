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
@Table(name = "operation_category")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OperationCategory {

    @EmbeddedId
    private OperationCategoryId id;

    @Column(name = "operation_category_registered_time")
    private Long registeredTime;

    private OperationCategory(final OperationCategoryId id, final Long registeredTime) {
        this.id = id;
        this.registeredTime = registeredTime;
    }

    public static OperationCategory of(final String operationId, final String categoryId) {
        return new OperationCategory(OperationCategoryId.of(operationId, categoryId),
            System.currentTimeMillis());
    }

    @Embeddable
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class OperationCategoryId implements Serializable {

        @Column(name = "operation_id")
        private String operationId;

        @Column(name = "category_id")
        private String categoryId;

        private OperationCategoryId(final String operationId, final String categoryId) {
            this.operationId = operationId;
            this.categoryId = categoryId;
        }

        public static OperationCategoryId of(final String operationId, final String categoryId) {
            return new OperationCategoryId(operationId, categoryId);
        }
    }

}
