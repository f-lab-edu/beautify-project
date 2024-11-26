package com.beautify_project.bp_app_api.entity;

import com.beautify_project.bp_app_api.utils.UUIDGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name="facility")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Facility {

    @Id
    @Column(name = "facility_id")
    private String id;

    private String name;
    private Long registered;


    public Facility(final String name, final Long registered) {
        this.id = UUIDGenerator.generate();
        this.name = name;
        this.registered = registered;
    }
}
