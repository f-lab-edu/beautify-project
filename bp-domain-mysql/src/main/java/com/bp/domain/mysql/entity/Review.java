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
@Table(name = "review")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @Column(name = "review_rate")
    private String rate;

    @Column(name = "review_content")
    private String content;

    @Column(name = "member_email")
    private String memberEmail;

    @Column(name = "operation_id")
    private Long operationId;

    @Column(name = "shop_id")
    private Long shopId;

    @Column(name = "reservation_id")
    private Long reservationId;

    private Review(final String rate, final String content, final String memberEmail,
        final Long operationId, final Long shopId, final Long reservationId) {
        this.rate = rate;
        this.content = content;
        this.memberEmail = memberEmail;
        this.operationId = operationId;
        this.shopId = shopId;
        this.reservationId = reservationId;
    }

    public static Review newReview(final String rate, final String content, final String memberEmail,
        final Long operationId, final Long shopId, final Long reservationId) {
        return new Review(rate, content, memberEmail, operationId, shopId, reservationId);
    }
}
