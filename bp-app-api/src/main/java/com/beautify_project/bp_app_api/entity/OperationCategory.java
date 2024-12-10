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
@Table(name = "operation_category")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OperationCategory {

    @Id
    @Column(name = "operation_category_id")
    private String id;

    @Column(name = "operation_id")
    private String operationId;

    @Column(name = "category_id")
    private String categoryId;

    @Column(name = "operation_category_registered_time")
    private Long registeredTime;

    private OperationCategory(final String id, final String operationId, final String categoryId,
        final Long registeredTime) {
        this.id = id;
        this.operationId = operationId;
        this.categoryId = categoryId;
        this.registeredTime = registeredTime;
    }

    public static OperationCategory of(final String operationId, final String categoryId) {
        return new OperationCategory(UUIDGenerator.generate(), operationId, categoryId,
            System.currentTimeMillis());
    }

}
