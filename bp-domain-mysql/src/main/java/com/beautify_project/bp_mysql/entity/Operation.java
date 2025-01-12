package com.beautify_project.bp_mysql.entity;

import com.beautify_project.bp_utils.UUIDGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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

    @Column(name = "operation_registered_time")
    private Long registeredTime;

    private Operation(final String id, final String name, final String description,
        final Long registeredTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.registeredTime = registeredTime;
    }

    public static Operation of(final String name, final String description) {
        return new Operation(UUIDGenerator.generate(), name, description, System.currentTimeMillis());
    }
}
