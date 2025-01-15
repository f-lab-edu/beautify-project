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
@Table(name = "category")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category {
    @Id
    @Column(name = "category_id")
    private String id;

    @Column(name = "category_name")
    private String name;

    @Column(name = "category_description")
    private String description;

    @Column(name = "category_registered_time")
    private Long registeredTime;

    private Category(final String id, final String name, final String description,
        final Long registeredTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.registeredTime = registeredTime;
    }

    public static Category of(final String name, final String description) {
        return new Category(UUIDGenerator.generateUUIDForEntity(), name, description,
            System.currentTimeMillis());
    }
}
