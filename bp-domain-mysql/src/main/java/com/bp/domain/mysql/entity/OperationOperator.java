package com.bp.domain.mysql.entity;

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
@Table(name = "operation_operator")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OperationOperator extends BaseEntity {

    @EmbeddedId
    private OperationOperatorId id;

    private OperationOperator(final OperationOperatorId id) {
        this.id = id;
    }

    public static OperationOperator OperationOperator(final Long operationId,
        final String operatorMail) {
        return new OperationOperator(
            OperationOperatorId.newOperationOperatorId(operationId, operatorMail));
    }

    @Embeddable
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class OperationOperatorId implements Serializable {

        @Column(name = "operation_id")
        private Long operationId;

        @Column(name = "operator_mail")
        private String operatorMail;

        private OperationOperatorId(final Long operationId, final String operatorMail) {
            this.operationId = operationId;
            this.operatorMail = operatorMail;
        }

        public static OperationOperatorId newOperationOperatorId(final Long operationId,
            final String operatorMail) {
            return new OperationOperatorId(operationId, operatorMail);
        }
    }
}
