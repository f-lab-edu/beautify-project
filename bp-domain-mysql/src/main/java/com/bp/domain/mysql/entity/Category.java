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
@Table(name = "category")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    @Column(name = "category_name")
    private String name;

    @Column(name = "category_description")
    private String description;

    @Column(name = "category_registered_time")
    private Long registeredTime;

    private Category(final String name, final String description, final Long registeredTime) {
        this.name = name;
        this.description = description;
        this.registeredTime = registeredTime;
    }

    public static Category newCategory(final String name, final String description) {
        return new Category(name, description, System.currentTimeMillis());
    }
}
