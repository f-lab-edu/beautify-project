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
@Table(name = "reservation")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation {

    @Id
    @Column(name = "reservation_id")
    private String id;

    @Column(name = "reservation_date")
    private Long date;

    @Column(name = "reservation_registered_time")
    private Long registeredTime;

    @Column(name = "reservation_member_email")
    private String memberEmail;

    @Column(name = "shop_id")
    private String shopId;

    @Column(name = "operation_id")
    private String operationId;

    private Reservation(final Long date, final Long registeredTime, final String memberEmail,
        final String shopId, final String operationId) {
        this.id = UUIDGenerator.generateUUIDForEntity();
        this.date = date;
        this.registeredTime = registeredTime;
        this.memberEmail = memberEmail;
        this.shopId = shopId;
        this.operationId = operationId;
    }

    public static Reservation of(final Long reservationDate,
        final String memberEmail, final String shopId, final String operationId) {
        return new Reservation(reservationDate, System.currentTimeMillis(), memberEmail, shopId,
            operationId);
    }
}
