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
@Table(name = "reservation")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long id;

    @Column(name = "reservation_date")
    private Long date;

    @Column(name = "reservation_member_email")
    private String memberEmail;

    @Column(name = "shop_id")
    private Long shopId;

    @Column(name = "operation_id")
    private Long operationId;

    private Reservation(final Long date, final String memberEmail,
        final Long shopId, final Long operationId) {
        this.date = date;
        this.memberEmail = memberEmail;
        this.shopId = shopId;
        this.operationId = operationId;
    }

    public static Reservation newReservation(final Long reservationDate,
        final String memberEmail, final Long shopId, final Long operationId) {
        return new Reservation(reservationDate, memberEmail, shopId, operationId);
    }
}
