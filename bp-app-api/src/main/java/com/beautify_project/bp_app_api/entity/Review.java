package com.beautify_project.bp_app_api.entity;

import com.beautify_project.bp_app_api.utils.UUIDGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "review")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review {

    @Id
    @Column(name = "review_id")
    @NotNull
    private String id;

    @Column(name = "review_rate")
    private String rate;

    @Column(name = "review_content")
    private String content;

    @Column(name = "review_registered")
    private Long registered;

    @Column(name = "member_email")
    @NotNull
    private String memberEmail;

    @Column(name = "operation_id")
    @NotNull
    private String operationId;

    @Column(name = "shop_id")
    @NotNull
    private String shopId;

    @Column(name = "reservation_id")
    @NotNull
    private String reservationId;

    @Builder
    private Review(final String rate, final String content, final Long registered,
        final String memberEmail,
        final String operationId, final String shopId, final String reservationId) {
        this.id = UUIDGenerator.generate();
        this.rate = rate;
        this.content = content;
        this.registered = registered;
        this.memberEmail = memberEmail;
        this.operationId = operationId;
        this.shopId = shopId;
        this.reservationId = reservationId;
    }
}
