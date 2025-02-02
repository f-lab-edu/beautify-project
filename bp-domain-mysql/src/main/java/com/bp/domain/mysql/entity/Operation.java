package com.bp.domain.mysql.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "operation")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Operation extends BaseEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "operation_id")
    private Long id;

    @Column(name = "operation_name")
    private String name;

    @Column(name = "operation_description")
    private String description;

    public Operation(final String name, final String description) {
        this.name = name;
        this.description = description;
    }

    public static Operation newOperation(final String name, final String description) {
        return new Operation(name, description);
    }
}
