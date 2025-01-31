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
@Table(name = "operation_category")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OperationCategory extends BaseEntity{

    @EmbeddedId
    private OperationCategoryId id;

    private OperationCategory(final OperationCategoryId id) {
        this.id = id;
    }

    public static OperationCategory newOperationCategory(final Long operationId, final Long categoryId) {
        return new OperationCategory(
            OperationCategoryId.newOperationCategoryId(operationId, categoryId));
    }

    @Embeddable
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class OperationCategoryId implements Serializable {

        @Column(name = "operation_id")
        private Long operationId;

        @Column(name = "category_id")
        private Long categoryId;

        private OperationCategoryId(final Long operationId, final Long categoryId) {
            this.operationId = operationId;
            this.categoryId = categoryId;
        }

        public static OperationCategoryId newOperationCategoryId(final Long operationId,
            final Long categoryId) {
            return new OperationCategoryId(operationId, categoryId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(operationId, categoryId);
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }

            final OperationCategoryId that = (OperationCategoryId) obj;
            return Objects.equals(operationId, that.categoryId) && Objects.equals(categoryId,
                that.categoryId);
        }
    }
}
