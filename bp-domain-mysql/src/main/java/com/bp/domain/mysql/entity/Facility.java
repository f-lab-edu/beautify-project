package com.bp.domain.mysql.entity;

import com.bp.utils.UUIDGenerator;
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

    @Column(name = "facility_registered_time")
    private Long registeredTime;

    private Facility(final String id, final String name, final Long registeredTime) {
        this.id = id;
        this.name = name;
        this.registeredTime = registeredTime;
    }

    public static Facility withName(final String name) {
        return new Facility(UUIDGenerator.generateUUIDForEntity(), name, System.currentTimeMillis());
    }
}
