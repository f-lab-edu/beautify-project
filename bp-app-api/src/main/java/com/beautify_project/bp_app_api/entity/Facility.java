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
@Table(name="facility")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Facility {

    @Id
    @Column(name = "facility_id")
    private String id;

    @Column(name = "facility_name")
    private String name;
    private Long registered;

    private Facility(final String name, final Long registered) {
        this.id = UUIDGenerator.generate();
        this.name = name;
        this.registered = registered;
    }

    public static Facility of(final String name, final Long registered) {
        return new Facility(name, registered);
    }
}
