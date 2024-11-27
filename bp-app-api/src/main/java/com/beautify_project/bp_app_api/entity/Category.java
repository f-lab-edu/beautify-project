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

    private Long registered;

    private Category(final String name, final String description, final Long registered) {
        this.id = UUIDGenerator.generate();
        this.name = name;
        this.description = description;
        this.registered = registered;
    }

    public static Category of(final String name, final String description, final Long registered) {
        return new Category(name, description, registered);
    }
}
