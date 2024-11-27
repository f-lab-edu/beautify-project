package com.beautify_project.bp_app_api.entity;

import com.beautify_project.bp_app_api.utils.UUIDGenerator;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "operation")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Operation {

    @Id
    @Column(name = "operation_id")
    private String id;

    @Column(name = "operation_name")
    private String name;

    @Column(name = "operation_description")
    private String description;

    private Long registered;

    @OneToMany(mappedBy = "operation", cascade = CascadeType.ALL)
    private final List<OperationCategory> operationCategories = new ArrayList<>();

    private Operation(final String name, final String description, final Long registered) {
        this.id = UUIDGenerator.generate();
        this.name = name;
        this.description = description;
        this.registered = registered;
    }

    public static Operation createOperation(final String name, final String description,
        final Long registered, List<Category> categories) {
        Operation newOperation = new Operation(name, description, registered);
        for (Category category : categories) {
            newOperation.addOperationCategory(
                OperationCategory.of(newOperation, category, registered));
        }
        return newOperation;
    }

    public void addOperationCategory(final OperationCategory operationCategory) {
        this.operationCategories.add(operationCategory);
    }
}
