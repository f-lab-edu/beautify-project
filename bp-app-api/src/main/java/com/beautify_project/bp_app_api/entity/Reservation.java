package com.beautify_project.bp_app_api.entity;

import com.beautify_project.bp_app_api.utils.UUIDGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
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

    private Long registered;

    @Column(name = "reservation_member_email")
    private String memberEmail;

    @Column(name = "reservation_shop_id")
    private String shopId;

    @Column(name = "reservation_operation_id")
    private String operationId;

    @Builder
    private Reservation(final Long date, final Long registered, final String memberEmail,
        final String shopId,
        final String operationId) {
        this.id = UUIDGenerator.generate();
        this.date = date;
        this.registered = registered;
        this.memberEmail = memberEmail;
        this.shopId = shopId;
        this.operationId = operationId;
    }
}
