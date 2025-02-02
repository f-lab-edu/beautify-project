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
@Table(name="facility")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Facility extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "facility_id")
    private Long id;

    @Column(name = "facility_name")
    private String name;

    private Facility(final String name) {
        this.name = name;
    }

    public static Facility newFacility(final String name) {
        return new Facility(name);
    }
}
