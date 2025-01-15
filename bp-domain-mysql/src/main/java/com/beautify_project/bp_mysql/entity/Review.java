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
@Table(name = "review")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review {

    @Id
    @Column(name = "review_id")
    private String id;

    @Column(name = "review_rate")
    private String rate;

    @Column(name = "review_content")
    private String content;

    @Column(name = "review_registered_time")
    private Long registeredTime;

    @Column(name = "member_email")
    private String memberEmail;

    @Column(name = "operation_id")
    private String operationId;

    @Column(name = "shop_id")
    private String shopId;

    @Column(name = "reservation_id")
    private String reservationId;

    private Review(final String id, final String rate, final String content,
        final Long registeredTime, final String memberEmail,
        final String operationId, final String shopId, final String reservationId) {
        this.id = id;
        this.rate = rate;
        this.content = content;
        this.registeredTime = registeredTime;
        this.memberEmail = memberEmail;
        this.operationId = operationId;
        this.shopId = shopId;
        this.reservationId = reservationId;
    }

    public static Review of(final String rate, final String content, final String memberEmail,
        final String operationId, final String shopId, final String reservationId) {
        return new Review(UUIDGenerator.generateUUIDForEntity(), rate, content, System.currentTimeMillis(),
            memberEmail, operationId, shopId, reservationId);
    }
}
